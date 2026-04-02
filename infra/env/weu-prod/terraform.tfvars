prefix    = "pagopa"
env       = "prod"
env_short = "p"

tags = {
  CreatedBy   = "Terraform"
  Environment = "Prod"
  Owner       = "pagoPA"
  Source      = "https://github.com/pagopa/pagopa-cruscotto-backend"
  CostCenter  = "TS310 - PAGAMENTI & SERVIZI"
}

apim_dns_zone_prefix               = "platform"
external_domain                    = "pagopa.it"
hostname = "crusc8.itn.internal.uat.platform.pagopa.it"

crusc8_aud = "5a3a6ad5-b4bc-49aa-a111-23279a0256ed" # 👀 https://pagopa.atlassian.net/wiki/spaces/IQCGJ/pages/2740420938/sso+sa-report+14-gen-2026#SrvPrincipal-su-EntraID-Crusc8
