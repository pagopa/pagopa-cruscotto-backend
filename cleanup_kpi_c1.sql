-- ========================================
-- ROLLBACK MANUALE COMPLETO VERSIONE 1.1.12
-- Rimuove TUTTE le modifiche della cartella 1.1.12
-- per permettere la riesecuzione COMPLETA dei changeset Liquibase
-- ========================================

-- FASE 1: ROLLBACK MANUALE DELLE STRUTTURE DATABASE
-- ========================================

-- 1.1 Rimuovi la configurazione KPI C.1 dalla tabella KPI_CONFIGURATION
-- CO_MODULE_ID = 12 è il modulo C.1
DELETE FROM KPI_CONFIGURATION WHERE CO_MODULE_ID = 12;

-- 1.2 DROP delle tabelle KPI C.1 (con CASCADE per rimuovere anche i constraint e i dati)
-- IMPORTANTE: Usare DROP TABLE per eliminare sia la struttura che i dati in un colpo solo
DROP TABLE IF EXISTS KPI_C1_ANALYTIC_DATA CASCADE;
DROP TABLE IF EXISTS KPI_C1_DETAIL_RESULT CASCADE;
DROP TABLE IF EXISTS KPI_C1_RESULT CASCADE;

-- 1.3 DROP delle sequence KPI C.1
-- Nota: Le sequence potrebbero avere nomi diversi, eliminiamo tutte le varianti possibili
DROP SEQUENCE IF EXISTS SQCRUSC8_KPIC1RESULT;
DROP SEQUENCE IF EXISTS SQCRUSC8_KPIC1DETARESU;
DROP SEQUENCE IF EXISTS SQCRUSC8_KPIC1ANALDATA;
DROP SEQUENCE IF EXISTS SEQ_KPI_C1_RESULT;
DROP SEQUENCE IF EXISTS SEQ_KPI_C1_DETAIL_RESULT;
DROP SEQUENCE IF EXISTS SEQ_KPI_C1_ANALYTIC_DATA;
DROP SEQUENCE IF EXISTS SQCRUSC8_KPIC1RESU;  -- Sequence troncata vista nei log

-- FASE 2: PULIZIA DATABASECHANGELOG KPI C.1
-- ========================================

-- 2.1 Rimuovi tutti i changeset KPI C.1 (ora rimane solo 20251104180004_add_kpi_c1_configuration.xml)
-- Eliminiamo anche i vecchi changeset separati che potrebbero essere nel database
DELETE FROM DATABASECHANGELOG 
WHERE FILENAME LIKE '%20251104180004_add_kpi_c1_configuration.xml%'
   OR FILENAME LIKE '%20251104180000_added_entity_KpiC1Result.xml%'
   OR FILENAME LIKE '%20251104180000_add_kpi_c1_configuration.xml%'
   OR FILENAME LIKE '%20251104180001_added_entity_KpiC1DetailResult.xml%'
   OR FILENAME LIKE '%20251104180002_added_entity_KpiC1AnalyticData.xml%';

-- FASE 3: VERIFICA POST-ROLLBACK
-- ========================================

-- 3.1 Verifica che i changeset KPI C.1 siano stati rimossi
SELECT COUNT(*) as "Changeset KPI C.1 rimasti" 
FROM DATABASECHANGELOG 
WHERE FILENAME LIKE '%KpiC1%' OR FILENAME LIKE '%kpi_c1%';

-- 3.2 Lista completa dei changeset KPI C.1 rimasti (dovrebbe essere vuoto)
SELECT ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED 
FROM DATABASECHANGELOG 
WHERE FILENAME LIKE '%KpiC1%' OR FILENAME LIKE '%kpi_c1%'
ORDER BY DATEEXECUTED DESC;

-- 3.3 Verifica che le tabelle KPI C.1 siano state eliminate
SELECT tablename 
FROM pg_tables 
WHERE schemaname = 'public' 
  AND tablename LIKE '%kpi_c1%'
ORDER BY tablename;

-- 3.4 Verifica che le sequence KPI C.1 siano state eliminate
SELECT sequence_name
FROM information_schema.sequences
WHERE sequence_schema = 'public'
  AND sequence_name LIKE '%kpic1%'
ORDER BY sequence_name;

-- ========================================
-- ISTRUZIONI POST-ESECUZIONE
-- ========================================
-- Dopo aver eseguito questo script:
-- 1. Verificare che tutte le query di verifica (FASE 3) ritornino 0 risultati
-- 2. Riavviare l'applicazione
-- 3. Liquibase eseguirà SOLO il changeset 20251104180004_add_kpi_c1_configuration.xml
--    (gli altri file duplicati sono stati eliminati dalla cartella /1.1.13ddl)
-- 4. Verificare nei log che il changeset venga eseguito con successo
-- ========================================

