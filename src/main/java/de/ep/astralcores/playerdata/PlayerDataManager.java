package de.ep.astralcores.playerdata;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.ep.astralcores.AstralCores;
import de.ep.astralcores.actionbar.ActionBarMode;
import de.ep.astralcores.core.CoreType;
import net.minecraft.server.level.ServerPlayer;

import java.io.File;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;

public class PlayerDataManager {

    // Caches player data in RAM
    private final Map<UUID, PlayerData> cache = new HashMap<>();

    // Database connection
    private Connection connection;

    // Gson utilities
    private final Gson gson = new Gson();
    private final Type listType = new TypeToken<ArrayList<String>>() {}.getType();
    private final Type cooldownMapType = new TypeToken<HashMap<CoreType, Integer>>() {}.getType();

    // Defines the current database schema
    private static final Map<String, String> DATABASE_COLUMNS = new LinkedHashMap<>();

    static {
        DATABASE_COLUMNS.put("uuid", "TEXT PRIMARY KEY");
        DATABASE_COLUMNS.put("equipped_core", "TEXT");
        DATABASE_COLUMNS.put("trusted_players", "TEXT");
        DATABASE_COLUMNS.put("actionbar_mode", "TEXT DEFAULT 'ICON'");
        DATABASE_COLUMNS.put("active_cooldowns", "TEXT DEFAULT '{}'");
        DATABASE_COLUMNS.put("passive_cooldowns", "TEXT DEFAULT '{}'");
    }

    // Initializes database folders and schema
    public PlayerDataManager(File worldFolder) {
        File dataFolder = new File(worldFolder, "astralcores");

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        try {
            Class.forName("org.sqlite.JDBC");

            File dbFile = new File(dataFolder, "playerdata.db");

            this.connection = DriverManager.getConnection(
                    "jdbc:sqlite:" + dbFile.getAbsolutePath()
            );

            initializeDatabase();

            AstralCores.LOGGER.info(
                    "AstralCores SQLite database loaded successfully."
            );

        } catch (Exception e) {
            AstralCores.LOGGER.error(
                    "Failed to initialize SQLite database.",
                    e
            );
        }
    }

    // Creates the database table and automatically adds missing columns
    private void initializeDatabase() throws SQLException {

        // Builds the table definition from the database column map
        StringBuilder createTable = new StringBuilder(
                "CREATE TABLE IF NOT EXISTS player_cores ("
        );

        boolean first = true;

        for (Map.Entry<String, String> entry : DATABASE_COLUMNS.entrySet()) {

            if (!first) {
                createTable.append(", ");
            }

            createTable
                    .append(entry.getKey())
                    .append(" ")
                    .append(entry.getValue());

            first = false;
        }

        createTable.append(")");

        try (Statement statement = connection.createStatement()) {
            statement.execute(createTable.toString());
        }

        // Reads the columns that already exist in the database
        Set<String> existingColumns = new HashSet<>();

        try (
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(
                        "PRAGMA table_info(player_cores)"
                )
        ) {
            while (rs.next()) {
                existingColumns.add(rs.getString("name"));
            }
        }

        // Adds columns that are missing from older database versions
        try (Statement statement = connection.createStatement()) {

            for (Map.Entry<String, String> entry : DATABASE_COLUMNS.entrySet()) {

                String columnName = entry.getKey();

                if (existingColumns.contains(columnName)) {
                    continue;
                }

                statement.execute(
                        "ALTER TABLE player_cores ADD COLUMN " +
                                columnName + " " +
                                entry.getValue()
                );

                AstralCores.LOGGER.info(
                        "Added missing database column '{}'.",
                        columnName
                );
            }
        }
    }

    // Loads persistent player data into the RAM cache
    public void load(ServerPlayer player) {
        UUID uuid = player.getUUID();

        String query =
                "SELECT * FROM player_cores WHERE uuid = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, uuid.toString());

            try (ResultSet rs = ps.executeQuery()) {

                PlayerData data = new PlayerData();

                if (rs.next()) {

                    String equipped = rs.getString("equipped_core");
                    String trustedJson = rs.getString("trusted_players");
                    String modeString = rs.getString("actionbar_mode");
                    String activeCooldownsJson =
                            rs.getString("active_cooldowns");
                    String passiveCooldownsJson =
                            rs.getString("passive_cooldowns");

                    // Loads the equipped core
                    if (equipped != null) {
                        try {
                            data.setEquippedCore(
                                    CoreType.valueOf(equipped)
                            );
                        } catch (IllegalArgumentException ignored) {
                        }
                    }

                    // Loads trusted players
                    if (trustedJson != null && !trustedJson.isEmpty()) {

                        List<String> trustedStrings =
                                gson.fromJson(
                                        trustedJson,
                                        listType
                                );

                        if (trustedStrings != null) {

                            for (String tUuid : trustedStrings) {

                                try {
                                    data.addTrustedPlayer(
                                            UUID.fromString(tUuid)
                                    );
                                } catch (IllegalArgumentException ignored) {
                                }
                            }
                        }
                    }

                    // Loads the actionbar mode
                    if (modeString != null) {

                        try {
                            data.setActionBarMode(
                                    ActionBarMode.valueOf(modeString)
                            );

                        } catch (IllegalArgumentException ignored) {
                            data.setActionBarMode(ActionBarMode.ICON);
                        }
                    }

                    // Loads active cooldowns
                    if (activeCooldownsJson != null
                            && !activeCooldownsJson.isEmpty()) {

                        Map<CoreType, Integer> activeMap =
                                gson.fromJson(
                                        activeCooldownsJson,
                                        cooldownMapType
                                );

                        if (activeMap != null) {
                            data.getActiveCooldownsMap()
                                    .putAll(activeMap);
                        }
                    }

                    // Loads passive cooldowns
                    if (passiveCooldownsJson != null
                            && !passiveCooldownsJson.isEmpty()) {

                        Map<CoreType, Integer> passiveMap =
                                gson.fromJson(
                                        passiveCooldownsJson,
                                        cooldownMapType
                                );

                        if (passiveMap != null) {
                            data.getPassiveCooldownsMap()
                                    .putAll(passiveMap);
                        }
                    }

                    AstralCores.LOGGER.info(
                            "Loaded player data for {}.",
                            player.getScoreboardName()
                    );

                } else {

                    // Creates a new player profile
                    insertNewPlayer(uuid);

                    AstralCores.LOGGER.info(
                            "Created player data for {}.",
                            player.getScoreboardName()
                    );
                }

                // Stores the player data in RAM
                cache.put(uuid, data);
            }

        } catch (SQLException e) {

            AstralCores.LOGGER.error(
                    "Failed to load player data for {}.",
                    uuid,
                    e
            );

            cache.put(uuid, new PlayerData());
        }
    }

    // Gets cached player data
    public PlayerData get(ServerPlayer player) {
        return cache.get(player.getUUID());
    }

    // Saves cached player data to the database
    public void save(ServerPlayer player) {

        UUID uuid = player.getUUID();
        PlayerData data = cache.get(uuid);

        if (data == null) {
            return;
        }

        String update =
                "UPDATE player_cores SET " +
                        "equipped_core = ?, " +
                        "trusted_players = ?, " +
                        "actionbar_mode = ?, " +
                        "active_cooldowns = ?, " +
                        "passive_cooldowns = ? " +
                        "WHERE uuid = ?";

        try (PreparedStatement ps =
                     connection.prepareStatement(update)) {

            // Saves the equipped core
            ps.setString(
                    1,
                    data.getEquippedCore() != null
                            ? data.getEquippedCore().name()
                            : null
            );

            // Saves trusted players
            List<String> trustedStrings = new ArrayList<>();

            for (UUID tUuid : data.getTrustedPlayers()) {
                trustedStrings.add(tUuid.toString());
            }

            ps.setString(
                    2,
                    gson.toJson(trustedStrings)
            );

            // Saves the actionbar mode
            ps.setString(
                    3,
                    data.getActionBarMode().name()
            );

            // Saves active cooldowns
            ps.setString(
                    4,
                    gson.toJson(
                            data.getActiveCooldownsMap()
                    )
            );

            // Saves passive cooldowns
            ps.setString(
                    5,
                    gson.toJson(
                            data.getPassiveCooldownsMap()
                    )
            );

            // Uses the player UUID to identify the row
            ps.setString(
                    6,
                    uuid.toString()
            );

            ps.executeUpdate();

        } catch (SQLException e) {

            AstralCores.LOGGER.error(
                    "Failed to save player data for {}.",
                    uuid,
                    e
            );
        }
    }

    // Saves and removes player data from the RAM cache
    public void unload(ServerPlayer player) {
        save(player);
        cache.remove(player.getUUID());
    }

    // Creates a new player database row
    private void insertNewPlayer(UUID uuid) throws SQLException {

        String insert =
                "INSERT INTO player_cores " +
                        "(uuid, equipped_core, trusted_players, actionbar_mode, " +
                        "active_cooldowns, passive_cooldowns) " +
                        "VALUES (?, NULL, '[]', 'ICON', '{}', '{}')";

        try (PreparedStatement ps =
                     connection.prepareStatement(insert)) {

            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        }
    }

    // Closes the database connection
    public void closeConnection() {

        try {

            if (connection != null && !connection.isClosed()) {

                connection.close();

                AstralCores.LOGGER.info(
                        "SQLite database connection closed."
                );
            }

        } catch (SQLException e) {

            AstralCores.LOGGER.error(
                    "Failed to close SQLite database connection.",
                    e
            );
        }
    }
}