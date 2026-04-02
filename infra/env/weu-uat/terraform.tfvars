prefix    = "pagopa"
env       = "uat"
env_short = "u"

tags = {
  CreatedBy   = "Terraform"
  Environment = "Uat"
  Owner       = "pagoPA"
  Source      = "https://github.com/pagopa/pagopa-cruscotto-backend"
  CostCenter  = "TS310 - PAGAMENTI & SERVIZI"
}

apim_dns_zone_prefix = "uat.platform"
external_domain      = "pagopa.it"
hostname             = "crusc8.itn.internal.uat.platform.pagopa.it"

crusc8_aud = "4ee1f987-9c92-4ad1-be17-df749dbba610" # 👀 https://pagopa.atlassian.net/wiki/spaces/IQCGJ/pages/2740420938/sso+sa-report+14-gen-2026#SrvPrincipal-su-EntraID-Crusc8
