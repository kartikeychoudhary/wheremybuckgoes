package kc.wheremybuckgoes.constants;


public class ApplicationConstant {
    private ApplicationConstant(){}
    public static final String JWT_PREFIX = "Bearer ";

    public enum Role {
        ADMIN,
        USER
    }

    public enum TokenType {
        BEARER
    }

    public enum TransactionType {
        DEBIT,
        CREDIT,
        PLACEHOLDER;
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

    public enum SplitType {
        EQUAL_SHARE,
        PERCENTAGE,
        UNEQUAL_SHARE,
        UNEQUALLY
    }

    public enum TaskStatus {
        OPEN,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }

    public enum TaskType {
        GenAi,
        Transaction
    }

    public enum AccountTypes {
        BANK,
        CREDIT_CARD,
        WALLET,
        LOAN;

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

    public static final String DATE_FORMAT = "DD-MM-YYYY";

    public static final String[] HEADERS = new String[]{"date", "description", "type",  "amount", "mode", "category", "account"};

    public static class Exceptions {
        private Exceptions(){}
        public static final String GEMINI_CALL_ERROR = "Exception occurred making api call to gemini";
        public static final String GEN_AI_MAX_REQUEST_THRESHOLD_EXCEEDS = "number of request exceeds maximum allowed gen ai request";
    }

    public static final String[] MONTHS = new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sept","Oct","Nov","Dec"};

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
