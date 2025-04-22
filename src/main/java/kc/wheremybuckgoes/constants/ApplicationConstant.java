/**
 * Constants and enumerations for the WhereMyBuckGoes application.
 *
 * This class serves as a centralized repository for application-wide constants,
 * enumerations, and static utility values. It provides standardized definitions
 * for various aspects of the application including:
 * - Authentication and security constants
 * - User roles and permissions
 * - Transaction and account type classifications
 * - Task status and type definitions
 * - UI theme options
 * - Date formatting standards
 * - Error message constants
 *
 * The class is designed as a non-instantiable utility class with only static
 * members, enforced by a private constructor.
 *
 * @author Kartikey Choudhary (kartikey31choudhary@gmail.com)
 * @version 1.0
 * @since 2024
 */

package kc.wheremybuckgoes.constants;


public class ApplicationConstant {
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ApplicationConstant(){}

    /**
     * JWT token prefix used in Authorization headers.
     * Follows the OAuth 2.0 Bearer Token standard.
     */
    public static final String JWT_PREFIX = "Bearer ";

    /**
     * User roles for authorization and access control.
     * Defines the permission levels within the application.
     */
    public enum Role {
        ADMIN,
        USER
    }

    /**
     * Token types supported by the authentication system.
     * Currently only supports Bearer tokens as per OAuth 2.0 standards.
     */
    public enum TokenType {
        BEARER
    }

    /**
     * Transaction types for financial operations.
     * Classifies financial movements as money in, money out, or placeholders.
     */
    public enum TransactionType {
        DEBIT,
        CREDIT,
        PLACEHOLDER;

        /**
         * Converts a string representation to the corresponding enum value.
         * Case-insensitive matching is supported.
         *
         * @param type String representation of the transaction type
         * @return The matching TransactionType enum value, or null if no match
         */
        public static TransactionType fromString(String type) {
            if (type != null) {
                for (TransactionType enumValue : TransactionType.values()) {
                    if (type.equalsIgnoreCase(enumValue.name())) {
                        return enumValue;
                    }
                }
            }
            return null;
        }
    }

    /**
     * Split types for shared expenses.
     * Defines how expenses can be divided among multiple users.
     */
    public enum SplitType {
        EQUAL_SHARE,
        PERCENTAGE,
        UNEQUAL_SHARE,
        UNEQUALLY
    }

    /**
     * Task processing statuses.
     * Tracks the lifecycle of asynchronous tasks in the system.
     */
    public enum TaskStatus {
        OPEN,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }

    /**
     * Task categories for different processing workflows.
     * Classifies tasks by their purpose and handling requirements.
     */
    public enum TaskType {
        GEN_AI,
        TRANSACTION
    }

    /**
     * Financial account classifications.
     * Categorizes different types of financial accounts tracked in the system.
     */
    public enum AccountTypes {
        BANK,
        CREDIT_CARD,
        WALLET,
        LOAN;

        /**
         * Converts a string representation to the corresponding enum value.
         * Case-insensitive matching is supported.
         *
         * @param type String representation of the account type
         * @return The matching AccountTypes enum value, or null if no match
         */
        public static AccountTypes fromString(String type) {
            if (type != null) {
                for (AccountTypes enumValue : AccountTypes.values()) {
                    if (type.equalsIgnoreCase(enumValue.name())) {
                        return enumValue;
                    }
                }
            }
            return null;
        }
    }

    /**
     * Standard date format used throughout the application.
     * Follows the day-month-year pattern with two-digit day and month.
     */
    public static final String DATE_FORMAT = "DD-MM-YYYY";

    /**
     * Column headers for CSV transaction imports/exports.
     * Defines the expected structure of transaction data files.
     */
    public static final String[] HEADERS = new String[]{"date", "description", "type",  "amount", "mode", "category", "account"};

    /**
     * Exception messages for standardized error handling.
     * Centralizes error message definitions for consistency.
     */
    public static class Exceptions {
        private Exceptions(){}
        public static final String GEMINI_CALL_ERROR = "Exception occurred making api call to gemini";
        public static final String GEN_AI_MAX_REQUEST_THRESHOLD_EXCEEDS = "number of request exceeds maximum allowed gen ai request";
    }

    /**
     * Month name abbreviations for date formatting and display.
     * Provides standardized three-letter abbreviations for all months.
     */
    public static final String[] MONTHS = new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sept","Oct","Nov","Dec"};


    /**
     * UI theme options available in the application.
     * Provides a wide range of visual styles for user customization.
     */
    public enum Theme {
        LIGHT,
        DARK,
        FOREST,
        CYBERPUNK,
        FANTASY,
        BLACK,
        LUXURY,
        DRACULA,
        CMYK,
        AUTUMN,
        BUSINESS,
        ACID,
        LEMONADE,
        NIGHT,
        COFFEE,
        WINTER,
        RETRO,
        SYNTHWAVE,
        HALLOWEEN,
        GARDEN,
        AQUA,
        LOFI,
        PASTEL,
        CORPORATE,
        VALENTINE,
        WIREFRAME,
        CUPCAKE,
        BUMBLEBEE,
        EMERALD,
        CARROT,
        SUNSET,
        NEUTRAL,
        GHOST,
        SLATE;

        /**
         * Converts a string representation to the corresponding enum value.
         * Case-insensitive matching is supported.
         * Falls back to LIGHT theme if no match is found.
         *
         * @param type String representation of the theme
         * @return The matching Theme enum value, or LIGHT if no match
         */
        public static Theme fromString(String type) {
            if (type != null) {
                for (Theme enumValue : Theme.values()) {
                    if (type.equalsIgnoreCase(enumValue.name())) {
                        return enumValue;
                    }
                }
            }
            return LIGHT;
        }
    }
}
