locals {
  repo_name = "pagopa-cruscotto-backend"

  display_name = "Cruscotto pagoPA backend service API"
  description = "Cruscotto pagoPA backend service API"
  path  = "smo/cruscotto"

  host         = "api.${var.apim_dns_zone_prefix}.${var.external_domain}"
  hostname     = var.hostname
}

resource "azurerm_api_management_group" "api_group" {
  name                = local.apim.product_id
  resource_group_name = local.apim.rg
  api_management_name = local.apim.name
  display_name        = local.display_name
  description         = local.description
}

resource "azurerm_api_management_api_version_set" "api_version_set" {
  name                = format("%s-${local.repo_name}", var.env_short)
  resource_group_name = local.apim.rg
  api_management_name = local.apim.name
  display_name        = local.display_name
  versioning_scheme   = "Segment"
}

module "api_v1" {
  source = "git::https://github.com/pagopa/terraform-azurerm-v3.git//api_management_api?ref=v8.62.1"

  name                  = format("%s-${local.repo_name}", var.env_short)
  api_management_name   = local.apim.name
  resource_group_name   = local.apim.rg
  product_ids           = [local.apim.product_id]
  subscription_required = false # via JWT

  version_set_id = azurerm_api_management_api_version_set.api_version_set.id
  api_version    = "v1"

  description  = local.description
  display_name = local.display_name
  path         = local.path
  protocols    = ["https"]

  service_url = null

  content_format = "openapi"
  content_value  = templatefile("../openapi/openapi.json", {
    host = local.host
  })

  xml_content = templatefile("./policy/_base_policy.xml", {
    hostname = var.hostname
  })
}

