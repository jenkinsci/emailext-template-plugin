#!/usr/bin/env groovy

/* `buildPlugin` step provided by: https://github.com/jenkins-infra/pipeline-library */
buildPlugin(
  // Container agents start faster and are easier to administer
  useContainerAgent: true,
  // Show failures on all configurations
  failFast: false,
  // Test Java 8 with default Jenkins version, Java 11 with a recent LTS, Java 17 even more recent
  configurations: [
    [platform: 'linux',   jdk: '21'],
    [platform: 'linux',   jdk: '17'],
    [platform: 'windows', jdk: '17']
  ]
)
