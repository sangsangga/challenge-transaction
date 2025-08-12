# Branch Protection and Quality Gates Setup

This document provides instructions for setting up branch protection rules and quality gates for the e-Banking Transaction API repository.

## 🛡️ Branch Protection Rules

### Main Branch Protection

Configure the following protection rules for the `main` branch in GitHub:

#### Required Settings

1. **Repository Settings** → **Branches** → **Add rule**

2. **Branch name pattern**: `main`

3. **Protection Rules**:
   - ✅ **Require a pull request before merging**
     - ✅ Require approvals: `1` (minimum)
     - ✅ Dismiss stale PR approvals when new commits are pushed
     - ✅ Require review from code owners (if CODEOWNERS file exists)
   
   - ✅ **Require status checks to pass before merging**
     - ✅ Require branches to be up to date before merging
     - **Required status checks**:
       - `test` (Test & Quality Checks)
       - `code-quality` (Code Quality Analysis)
       - `security-scan` (Security Scan)
       - `build-artifact` (Build Application)
   
   - ✅ **Require conversation resolution before merging**
   
   - ✅ **Require signed commits** (recommended for production)
   
   - ✅ **Require linear history** (optional, prevents merge commits)
   
   - ✅ **Include administrators** (applies rules to admins too)
   
   - ❌ **Allow force pushes** (disabled for safety)
   
   - ❌ **Allow deletions** (prevents accidental branch deletion)

### Develop Branch Protection (Optional)

For teams using GitFlow:

1. **Branch name pattern**: `develop`
2. **Protection Rules**:
   - ✅ Require pull request reviews: `1`
   - ✅ Require status checks: `test`, `code-quality`, `security-scan`
   - ✅ Require conversation resolution

## 🔍 Quality Gates Configuration

### SonarCloud Integration

1. **Setup SonarCloud Project**:
   ```bash
   # Visit https://sonarcloud.io
   # Import your GitHub repository
   # Copy the project key and organization
   ```

2. **Add GitHub Secrets**:
   - `SONAR_TOKEN`: Your SonarCloud token
   - Update `ci.yml` with correct project key and organization

3. **Quality Gate Conditions**:
   - Coverage: > 80%
   - Duplicated Lines: < 3%
   - Maintainability Rating: A
   - Reliability Rating: A
   - Security Rating: A
   - Security Hotspots: 100% reviewed

### OWASP Dependency Check

The CI pipeline includes OWASP dependency checking with:
- **CVSS Threshold**: 7.0 (High/Critical vulnerabilities fail the build)
- **Suppressions**: Configure in `.github/OWASP-suppressions.xml`

### Code Coverage Requirements

Add to `pom.xml` for enforced coverage:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <execution>
            <id>check</id>
            <phase>test</phase>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## 🔐 Required GitHub Secrets

Configure these secrets in **Repository Settings** → **Secrets and variables** → **Actions**:

### Docker Registry
- `DOCKERHUB_USERNAME`: Your DockerHub username
- `DOCKERHUB_TOKEN`: DockerHub access token (not password)

### Kubernetes Deployment
- `KUBE_CONFIG_STAGING`: Base64 encoded kubeconfig for staging cluster
- `KUBE_CONFIG_PRODUCTION`: Base64 encoded kubeconfig for production cluster

### Code Quality
- `SONAR_TOKEN`: SonarCloud authentication token

### Notifications
- `SLACK_WEBHOOK`: Slack webhook URL for deployment notifications
- `TEAMS_WEBHOOK`: Microsoft Teams webhook URL (optional)

## 📋 Environment Variables

### Repository Variables
Configure in **Repository Settings** → **Secrets and variables** → **Actions** → **Variables**:

- `REGISTRY_URL`: Docker registry URL (default: docker.io)
- `IMAGE_NAME`: Docker image name (default: ebanking-transaction-api)
- `JAVA_VERSION`: Java version for builds (default: 17)

## 🚦 Status Check Configuration

### Required Checks for PRs

The following GitHub Actions jobs must pass before merging:

1. **test** - Runs unit and integration tests
2. **code-quality** - SonarCloud analysis
3. **security-scan** - OWASP dependency check and Trivy scan
4. **build-artifact** - Successful application build

### Optional Checks

- **docker-build** - Only runs on push to main
- **deploy-staging** - Only runs on main branch
- **deploy-production** - Only runs on tagged releases

## 🏷️ Semantic Versioning

### Tag Format
Use semantic versioning tags: `v1.0.0`, `v1.1.0`, `v2.0.0-beta.1`

### Automated Tagging (Optional)
Consider using semantic-release for automated versioning:

```yaml
# .github/workflows/semantic-release.yml
name: Semantic Release
on:
  push:
    branches: [main]
jobs:
  release:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v4
      with:
        token: ${{ secrets.GITHUB_TOKEN }}
    - uses: actions/setup-node@v3
    - run: npx semantic-release
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

## 🔄 Workflow Permissions

Ensure GitHub Actions has proper permissions:

1. **Repository Settings** → **Actions** → **General**
2. **Workflow permissions**: 
   - ✅ Read and write permissions
   - ✅ Allow GitHub Actions to create and approve pull requests

## 📊 Monitoring and Alerts

### Failed Build Notifications

Configure notifications for:
- Failed CI builds
- Security vulnerabilities found
- Deployment failures
- Coverage drops below threshold

### Dashboard Setup

Consider setting up:
- GitHub Projects for issue tracking
- Grafana dashboard for deployment metrics
- SonarCloud dashboard for code quality trends

## 🧪 Testing the Setup

1. **Create a test PR**:
   ```bash
   git checkout -b test-protection
   echo "# Test" >> TEST.md
   git add TEST.md
   git commit -m "test: verify branch protection"
   git push -u origin test-protection
   ```

2. **Verify all checks run**
3. **Ensure PR cannot be merged without approvals**
4. **Test that force push is blocked**

## 📝 Maintenance

### Regular Tasks

- **Weekly**: Review security scan results
- **Monthly**: Update dependency versions
- **Quarterly**: Review and update quality gate thresholds
- **As needed**: Update OWASP suppressions for false positives

### Troubleshooting

Common issues and solutions:

1. **Status checks not appearing**: Check workflow names match exactly
2. **Docker push failing**: Verify DOCKERHUB credentials
3. **Kubernetes deployment failing**: Check kubeconfig and cluster access
4. **SonarCloud failing**: Verify token and project configuration

---

**Note**: After implementing these settings, all contributors will need to follow the pull request workflow for any changes to the main branch.
