package com.nexigroup.pagopa.cruscotto.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String PRINCIPAL = "principal";

    public static final String ADMIN = "ROLE_SUPER_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final String GTW_INFO_ACCOUNT = "GTW.INFO_ACCOUNT";

    public static final String GTW_MODIFICA_PASSWORD = "GTW.MODIFICA_PASSWORD";

    public static final String GTW_MODIFICA_PROFILO_ACCOUNT = "GTW.MODIFICA_PROFILO_ACCOUNT";

    public static final String GTW_AUDIT = "GTW.AUDIT";

    public static final String GTW_STRUMENTI_CONTROLLO = "GTW.STRUMENTI_CONTROLLO";

    public static final String GTW_INFO_APP = "GTW.INFO_APP";

    public static final String GTW_ADMIN_LIST_USER = "GTW.ADMIN_LIST_USER";

    public static final String GTW_ADMIN_DETAIL_USER = "GTW.ADMIN_DETAIL_USER";

    public static final String GTW_ADMIN_CREATE_USER = "GTW.ADMIN_CREATE_USER";

    public static final String GTW_ADMIN_UPDATE_USER = "GTW.ADMIN_UPDATE_USER";

    public static final String GTW_UPDATE_USER_ASSOCIA_CLIENTI = "GTW.UPDATE_USER_ASSOCIA_CLIENTI";

    public static final String GTW_UPDATE_USER_RIMUOVI_CLIENTE = "GTW.UPDATE_USER_RIMUOVI_CLIENTE";

    public static final String GTW_ADMIN_DELETE_USER = "GTW.ADMIN_DELETE_USER";

    public static final String GTW_ADMIN_LIST_PERMISSION = "GTW.ADMIN_LIST_PERMISSION";

    public static final String GTW_ELENCO_PERMESSI_ASSOCIABILI_A_FUNZIONE = "GTW.ELENCO_PERMESSI_ASSOCIABILI_A_FUNZIONE";

    public static final String GTW_ELENCO_PERMESSI_ASSOCIATI_A_FUNZIONE = "GTW.ELENCO_PERMESSI_ASSOCIATI_A_FUNZIONE";

    public static final String GTW_ADMIN_UPDATE_PERMISSION = "GTW.ADMIN_UPDATE_PERMISSION";

    public static final String GTW_ADMIN_CREATE_PERMISSION = "GTW.ADMIN_CREATE_PERMISSION";

    public static final String GTW_ADMIN_DETAIL_PERMISSION = "GTW.ADMIN_DETAIL_PERMISSION";

    public static final String GTW_ADMIN_DELETE_PERMISSION = "GTW.ADMIN_DELETE_PERMISSION";

    public static final String GTW_LIST_FUNCTION = "GTW.LIST_FUNCTION";

    public static final String GTW_UPDATE_FUNCTION = "GTW.UPDATE_FUNCTION";

    public static final String GTW_ELENCO_FUNZIONI_ASSOCIABILI_A_GRUPPO = "GTW.ELENCO_FUNZIONI_ASSOCIABILI_A_GRUPPO";

    public static final String GTW_ELENCO_FUNZIONI_ASSOCIATI_A_GRUPPO = "GTW.ELENCO_FUNZIONI_ASSOCIATI_A_GRUPPO";

    public static final String GTW_UPDATE_FUNCTION_ASSOCIA_PERMESSO = "GTW.UPDATE_FUNCTION_ASSOCIA_PERMESSO";

    public static final String GTW_UPDATE_FUNCTION_RIMUOVI_PERMESSO = "GTW.UPDATE_FUNCTION_RIMUOVI_PERMESSO";

    public static final String GTW_CREATE_FUNCTION = "GTW.CREATE_FUNCTION";

    public static final String GTW_DETAIL_FUNCTION = "GTW.DETAIL_FUNCTION";

    public static final String GTW_DELETE_FUNCTION = "GTW.DELETE_FUNCTION";

    public static final String GTW_LIST_GROUP = "GTW.LIST_GROUP";

    public static final String GTW_UPDATE_GROUP = "GTW.UPDATE_GROUP";

    public static final String GTW_UPDATE_GROUP_ASSOCIA_FUNZIONE = "GTW.UPDATE_GROUP_ASSOCIA_FUNZIONE";

    public static final String GTW_UPDATE_GROUP_RIMUOVI_FUNZIONE = "GTW.UPDATE_GROUP_RIMUOVI_FUNZIONE";

    public static final String GTW_CREATE_GROUP = "GTW.CREATE_GROUP";

    public static final String GTW_DETAIL_GROUP = "GTW.DETAIL_GROUP";

    public static final String GTW_DELETE_GROUP = "GTW.DELETE_GROUP";

    public static final String UNKNOWN = "UNKNOWN";

    public static final String GTW_LIST_CLIENTI = "GTW.LIST_CLIENTI";

    public static final String GTW_LIST_CLIENTI_ASSOCIATI_USER = "GTW.LIST_CLIENTI_ASSOCIATI_USER";

    public static final String GTW_LIST_CLIENTI_ASSOCIABILI_USER = "GTW.LIST_CLIENTI_ASSOCIABILI_USER";

    public static final String GTW_LIST_CLIENTI_USER_ABIL = "GTW.LIST_CLIENTI_USER_ABIL";

    public static final String GTW_UPDATE_CLIENTE = "GTW.UPDATE_CLIENTE";

    public static final String GTW_CREATE_CLIENTE = "GTW.CREATE_CLIENTE";

    public static final String GTW_DETAIL_CLIENTE = "GTW.DETAIL_CLIENTE";

    public static final String GTW_DELETE_CLIENTE = "GTW.DELETE_CLIENTE";

    public static final String GTW_LIST_TENANT = "GTW.LIST_TENANT";

    public static final String GTW_DETAIL_TENANT = "GTW.DETAIL_TENANT";

    public static final String GTW_CREATE_TENANT = "GTW.CREATE_TENANT";

    public static final String GTW_UPDATE_TENANT = "GTW.UPDATE_TENANT";

    public static final String GTW_DELETE_TENANT = "GTW.DELETE_TENANT";

    public static final String CREATE_DATASOURCE = "GTW.CREATE_DTSOURCE";

    public static final String UPDATE_DATASOURCE = "GTW.UPDATE_DTSOURCE";

    public static final String LIST_DATASOURCE = "GTW.LIST_DTSOURCE";

    public static final String DETAIL_DATASOURCE = "GTW.DETAIL_DTSOURCE";

    public static final String DELETE_DATASOURCE = "GTW.DELETE_DTSOURCE";

    public static final String GTW_QUERY_ESEGUITE = "GTW.QUERY_ESEGUITE";

    public static final String GTW_LIST_COMPANIES = "GTW.LIST_COMPANIES";

    public static final String GTW_DETAIL_COMPANY = "GTW.DETAIL_COMPANY";

    public static final String GTW_LIST_BANCA = "GTW.LIST_BANCA";
    public static final String GTW_DETAIL_BANCA = "GTW.DETAIL_BANCA";
    public static final String GTW_CREATE_BANCA = "GTW.CREATE_BANCA";
    public static final String GTW_UPDATE_BANCA = "GTW.UPDATE_BANCA";
    public static final String GTW_DELETE_BANCA = "GTW.DELETE_BANCA";

    public static final String GTW_LIST_STEP = "GTW.LIST_STEP";

    public static final String GTW_LIST_FLUSSI_DISP = "GTW.LIST_FLUSSI_DISP";

    public static final String GTW_DETAIL_FLUSSI_DISP = "GTW.DETAIL_FLUSSI_DISP";

    public static final String GTW_CREATE_FLUSSI_DISP = "GTW.CREATE_FLUSSI_DISP";

    public static final String GTW_UPDATE_FLUSSI_DISP = "GTW.UPDATE_FLUSSI_DISP";

    public static final String GTW_DELETE_FLUSSI_DISP = "GTW.DELETE_FLUSSI_DISP";

    public static final String GTW_DOWNLOAD_FLUSSI_DISP = "GTW.DOWNLOAD_FLUSSI_DISP";

    public static final String GTW_LIST_FLUSSI_BKIT = "GTW.LIST_FLUSSI_BKIT";

    public static final String GTW_DETAIL_FLUSSI_BKIT = "GTW.DETAIL_FLUSSI_BKIT";

    public static final String GTW_CREATE_FLUSSI_BKIT = "GTW.CREATE_FLUSSI_BKIT";

    public static final String GTW_UPDATE_FLUSSI_BKIT = "GTW.UPDATE_FLUSSI_BKIT";

    public static final String GTW_DELETE_FLUSSI_BKIT = "GTW.DELETE_FLUSSI_BKIT";

    public static final String GTW_DOWNLOAD_FLUSSI_BKIT = "GTW.DOWNLOAD_FLUSSI_DISP_BKIT";

    public static final String GTW_LIST_FLUSSI_ENTE = "GTW.LIST_FLUSSI_ENTE";

    public static final String GTW_DETAIL_FLUSSI_ENTE = "GTW.DETAIL_FLUSSI_ENTE";

    public static final String GTW_CREATE_FLUSSI_ENTE = "GTW.CREATE_FLUSSI_ENTE";

    public static final String GTW_UPDATE_FLUSSI_ENTE = "GTW.UPDATE_FLUSSI_ENTE";

    public static final String GTW_DELETE_FLUSSI_ENTE = "GTW.DELETE_FLUSSI_ENTE";

    public static final String GTW_DOWNLOAD_FLUSSI_ENTE = "GTW.DOWNLOAD_FLUSSI_ENTE";

    public static final String GTW_LIST_ACK_BKIT = "GTW.LIST_ACK_BKIT";

    public static final String GTW_DETAIL_ACK_BKIT = "GTW.DETAIL_ACK_BKIT";

    public static final String GTW_DOWNLOAD_ACK_DISP_BKIT = "GTW.DOWNLOAD_ACK_DISP_BKIT";

    public static final String GTW_CREATE_ACK_BKIT = "GTW.CREATE_ACK_BKIT";

    public static final String GTW_UPDATE_ACK_BKIT = "GTW.UPDATE_ACK_BKIT";

    public static final String GTW_DELETE_ACK_BKIT = "GTW.DELETE_ACK_BKIT";

    public static final String GTW_FIRMA_FILE_AMMINISTRAZIONE = "GTW.FIRMA_FILE_AMMINISTRAZIONE";

    public static final String QUERY_CUBE = "VIS.QUERY_CUBE";

    public static final String LIST_DASHBOARD = "VIS.LIS_DASHBOARD";

    public static final String DET_DASHBOARD = "VIS.DET_DASHBOARD";

    public static final String DELETE_DASHBOARD = "VIS.DEL_DASHBOARD";

    public static final String UPDATE_DASHBOARD = "VIS.UPD_DASHBOARD";

    public static final String UPDATE_CHART = "VIS.UPD_CHART";

    public static final String DELETE_CHART = "VIS.DEL_CHART";

    public static final String FLC_HEALTH = "FLC.HEALTH";

    public static final String FLC_METRIC = "FLC.METRIC";

    public static final String FLC_INFO_APP = "FLC.INFO_APP";

    public static final String FLC_LOGS = "FLC.LOGS";

    public static final String FLC_CONFIGURATION = "FLC.CONFIGURATION";

    public static final String FLC_LIST_HOST = "FLC.LIST_HOST"; //

    public static final String FLC_DETAIL_HOST = "FLC.DETAIL_HOST"; //

    public static final String FLC_DOWNLOAD_HOST = "FLC.DOWNLOAD_HOST"; //

    public static final String FLC_LIST_PROCESSO = "FLC.LIST_PROCESSO"; //

    public static final String FLC_UPDATE_PROCESSO = "FLC.UPDATE_PROCESSO"; //

    public static final String FLC_CREATE_PROCESSO = "FLC.CREATE_PROCESSO"; //

    public static final String FLC_DETAIL_PROCESSO = "FLC.DETAIL_PROCESSO"; //

    public static final String FLC_LIST_REPORT = "FLC.LIST_REPORT"; //

    public static final String FLC_LIST_REPORT_DETAIL_TABULATO = "FLC.LIST_REPORT_DETAIL_TABULATO"; //

    public static final String FLC_DETAIL_REPORT = "FLC.DETAIL_REPORT"; //

    public static final String FLC_DELETE_REPORT = "FLC.DELETE_REPORT"; //

    public static final String FLC_DOWNLOAD_REPORT = "FLC.DOWNLOAD_REPORT"; //

    public static final String FLC_LIST_TABULATO = "FLC.LIST_TABULATO"; //

    public static final String FLC_LIST_TABULATO_DETAIL_FLUSSO_HOST = "FLC.LIST_TABULATO_DETAIL_FLUSSO_HOST"; //

    public static final String FLC_DOWNLOAD_TABULATO = "FLC.DOWNLOAD_TABULATO"; //

    public static final String FLC_DETAIL_TABULATO = "FLC.DETAIL_TABULATO"; //

    public static final String FLC_LIST_PROCESSO_TABULATO = "FLC.LIST_PROCESSO_TABULATO"; //

    public static final String FLC_UPDATE_PROCESSO_TABULATO = "FLC.UPDATE_PROCESSO_TABULATO"; //

    public static final String FLC_CREATE_PROCESSO_TABULATO = "FLC.CREATE_PROCESSO_TABULATO"; //

    public static final String FLC_DETAIL_PROCESSO_TABULATO = "FLC.DETAIL_PROCESSO_TABULATO"; //

    public static final String FLC_LIST_TIPO_TABULATO = "FLC.LIST_TIPO_TABULATO"; //

    public static final String FLC_UPDATE_TIPO_TABULATO = "FLC.UPDATE_TIPO_TABULATO"; //

    public static final String FLC_CREATE_TIPO_TABULATO = "FLC.CREATE_TIPO_TABULATO"; //

    public static final String FLC_DETAIL_TIPO_TABULATO = "FLC.DETAIL_TIPO_TABULATO"; //

    public static final String FLC_DELETE_TIPO_TABULATO = "FLC.DELETE_TIPO_TABULATO"; //

    public static final String FLC_TIPO_TABULATO_ENTI_DA_ASSOCIARE = "FLC.TIPO_TABULATO_ENTI_DA_ASSOCIARE"; //

    public static final String FLC_TIPO_TABULATO_ENTI_ASSOCIATI = "FLC.TIPO_TABULATO_ENTI_ASSOCIATI"; //

    public static final String FLC_TIPO_TABULATO_ASSOCIA_ENTI = "FLC.TIPO_TABULATO_ASSOCIA_ENTI"; //

    public static final String FLC_TIPO_TABULATO_DISSOCIA_ENTI = "FLC.TIPO_TABULATO_DISSOCIA_ENTI"; //

    public static final String FLC_LIST_TEMPLATE = "FLC.LIST_TEMPLATE"; //

    public static final String FLC_UPDATE_TEMPLATE = "FLC.UPDATE_TEMPLATE"; //

    public static final String FLC_CREATE_TEMPLATE = "FLC.CREATE_TEMPLATE"; //

    public static final String FLC_DETAIL_TEMPLATE = "FLC.DETAIL_TEMPLATE"; //

    public static final String FLC_DELETE_TEMPLATE = "FLC.DELETE_TEMPLATE"; //

    public static final String FLC_DOWNLOAD_TEMPLATE = "FLC.DOWNLOAD_TEMPLATE"; //

    public static final String FLC_LIST_ISTITUTI_ABILITATI = "FLC.LIST_ISTITUTI_ABILITATI"; //

    public static final String FLC_JOBS = "FLC.JOBS"; //

    public static final String FLC_LIST_ENTI = "FLC.LIST_ENTI"; //

    public static final String FLC_LIST_ENTI_ABILITATI = "FLC.LIST_ENTI_ABILITATI"; //

    public static final String LIST_SERVICE = "GTW.LIST_SERVICE";

    public static final String LIST_SUB_SERVICE = "GTW.LIST_SUB_SERVICE";

    private AuthoritiesConstants() {}
}
