prefix    = "pagopa"
env       = "dev"
env_short = "d"

tags = {
  CreatedBy   = "Terraform"
  Environment = "Dev"
  Owner       = "pagoPA"
  Source      = "https://github.com/pagopa/pagopa-cruscotto-backend"
  CostCenter  = "TS310 - PAGAMENTI & SERVIZI"
}

apim_dns_zone_prefix               = "dev.platform"
external_domain                    = "pagopa.it"
hostname = "crusc8.itn.internal.dev.platform.pagopa.it"

crusc8_aud = "10eb962e-531c-4f0c-b216-bb0539bba43b" # 👀 https://pagopa.atlassian.net/wiki/spaces/IQCGJ/pages/2740420938/sso+sa-report+14-gen-2026#SrvPrincipal-su-EntraID-Crusc8
