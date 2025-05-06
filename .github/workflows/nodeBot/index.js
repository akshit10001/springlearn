const { Octokit } = require("@octokit/rest");
const { createAppAuth } = require("@octokit/auth-app");

// Add debug logging
console.log('Starting PR Review Bot...');
console.log('Environment variables:', {
  appId: process.env.APP_ID ? 'Set' : 'Not set',
  privateKey: process.env.PRIVATE_KEY ? 'Set' : 'Not set',
  installationId: process.env.INSTALLATION_ID ? 'Set' : 'Not set'
});

const appId = process.env.APP_ID;
const privateKey = process.env.PRIVATE_KEY;
const installationId = process.env.INSTALLATION_ID;

// Add main execution
async function main() {
  try {
    console.log('Initializing Octokit...');
    const octokit = new Octokit({
      authStrategy: createAppAuth,
      auth: {
        appId,
        privateKey,
        installationId,
      },
    });

    // Get PR number from environment
    const prNumber = process.env.PR_NUMBER || process.env.GITHUB_EVENT_PATH;
    console.log('PR Number:', prNumber);

    // Get repository info from environment
    const owner = process.env.GITHUB_REPOSITORY?.split('/')[0];
    const repo = process.env.GITHUB_REPOSITORY?.split('/')[1];
    console.log('Repository:', { owner, repo });

    if (!prNumber || !owner || !repo) {
      throw new Error('Missing required environment variables');
    }

    await reviewPR(owner, repo, prNumber);
    console.log('PR review completed successfully');
  } catch (error) {
    console.error('Error in PR review:', error);
    process.exit(1);
  }
}

// ... rest of your existing reviewPR function ...

// Call main function
main().catch(console.error);

const octokit = new Octokit({
  authStrategy: createAppAuth,
  auth: {
    appId,
    privateKey,
    installationId,
  },
});

async function reviewPR(owner, repo, pull_number) {
  const { data: files } = await octokit.pulls.listFiles({ owner, repo, pull_number });
  console.log(files);
  const comments = [];
  for (const file of files) {
    if (file.filename.endsWith("UserController.java")) {
      const patch = file.patch.split("\n");
      patch.forEach((line, index) => {
        if (line.includes("throws Exception")) {
          comments.push({
            path: file.filename,
            line: index + 1,
            body: "Avoid using `throws Exception`. Use specific exception types instead.",
          });
        }
        if (line.includes("email.isEmpty() || email.length() == 0")) {
          comments.push({
            path: file.filename,
            line: index + 1,
            body: "Use `StringUtils.isEmpty(email)` for better null/empty checks.",
          });
        }
      });
    }
  }

  if (comments.length > 0) {
    await octokit.pulls.createReview({
      owner,
      repo,
      pull_number,
      body: "Automated review comments:",
      event: "REQUEST_CHANGES",
      comments,
    });
  } else {
    await octokit.pulls.createReview({
      owner,
      repo,
      pull_number,
      body: "No issues found in the PR.",
      event: "APPROVE",
    });
  }
}

// Export using CommonJS
module.exports = { reviewPR };