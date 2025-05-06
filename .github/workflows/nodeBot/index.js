const { execSync } = require('child_process');
const { context, getOctokit } = require('@actions/github');
const core = require('@actions/core');

async function run() {
  try {
    const githubToken = process.env.GITHUB_TOKEN;
    const octokit = getOctokit(githubToken);

    // Get repository and PR information
    const { owner, repo } = context.repo;
    const prNumber = context.payload.pull_request?.number;

    if (!prNumber) {
      throw new Error('This action can only be run on pull requests');
    }

    // Run build and tests
    try {
      execSync('mvn clean install', { stdio: 'inherit' });
      console.log('Build and tests completed successfully');
    } catch (error) {
      await createComment(octokit, owner, repo, prNumber, '❌ Build or tests failed. Please check the logs for details.');
      throw error;
    }

    // Create success comment
    await createComment(octokit, owner, repo, prNumber, '✅ Build and tests passed successfully!');

  } catch (error) {
    core.setFailed(error.message);
  }
}

async function createComment(octokit, owner, repo, issueNumber, body) {
  await octokit.rest.issues.createComment({
    owner,
    repo,
    issue_number: issueNumber,
    body
  });
}

run();