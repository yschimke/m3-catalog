package ee.schimke.m3catalog.typed

import ee.schimke.composeai.discovery.UiBuilderPolicyFile

// Vendored samples retain their discovery-owned inventory; only catalog identity is authored.
fun m3SamplesPolicy(): UiBuilderPolicyFile =
  UiBuilderPolicyFile.Builder("compose-ui-builder-policy/v1", "mobile")
    .apply {
      jsonSchema =
        "https://raw.githubusercontent.com/yschimke/compose-ai-tools/main/scripts/design-artifacts/ui-builder.policy.schema.json"
      catalogId = "m3-samples"
      platformLabel = "Mobile"
    }
    .build()
