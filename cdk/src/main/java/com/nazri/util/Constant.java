package com.nazri.util;

public class Constant {

    private Constant() {
        // restrict instantiation
    }

    public static final String PROJECT = "project";
    public static final String WARRANTY_TRACKER = "warranty-tracker";
    public static final String ENVIRONMENT = "environment";

    public static final String DEV = "dev";
    public static final String SIT = "sit";
    public static final String UAT = "uat";
    public static final String PRD = "prd";

    public static final String DEV_DB_URL = System.getenv("SUPABASE_DB_URL");
    public static final String DEV_DB_USERNAME = System.getenv("SUPABASE_DB_USER");
    public static final String DEV_DB_PASSWORD = System.getenv("SUPABASE_DB_PASSWORD");

    public static final String PROD_DB_URL = System.getenv("SUPABASE_PROD_DB_URL");
    public static final String PROD_DB_USERNAME = System.getenv("SUPABASE_PROD_DB_USER");
    public static final String PROD_DB_PASSWORD = System.getenv("SUPABASE_PROD_DB_PASSWORD");
}
