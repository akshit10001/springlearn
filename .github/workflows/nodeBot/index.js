const { Octokit } = require("@octokit/rest");
const { createAppAuth } = require("@octokit/auth-app");
const fs = require('fs');

// Initialize environment variables
const appId = process.env.APP_ID;
const privateKey = process.env.PRIVATE_KEY;
const installationId = process.env.INSTALLATION_ID;

// Initialize Octokit at the top level
const octokit = new Octokit({
  authStrategy: createAppAuth,
  auth: {
    appId,
    privateKey,
    installationId,
  },
});

async function reviewPR(octokit, owner, repo, pull_number) {
  console.log(`Reviewing PR #${pull_number} in ${owner}/${repo}`);
  
  try {
    // Get the PR details to get the latest commit SHA
    const { data: pullRequest } = await octokit.pulls.get({
      owner,
      repo,
      pull_number,
    });

    const { data: files } = await octokit.pulls.listFiles({
      owner,
      repo,
      pull_number,
    });
    
    console.log(`Found ${files.length} files to review`);
    const comments = [];

    for (const file of files) {
      if (file.filename.endsWith("UserController.java")) {
        // Track position in the diff
        const patch = file.patch.split("\n");
        let position = 0;

        patch.forEach((line) => {
          position++;
          
          if (line.startsWith("+")) {
            if (line.includes("throws Exception")) {
              comments.push({
                path: file.filename,
                position: position,
                body: "🚨 Avoid using `throws Exception`. Use specific exception types instead to provide better error handling and documentation."
              });
              console.log(`Adding comment at position ${position}: ${line}`);
            }
            if (line.includes("email.isEmpty() || email.length() == 0")) {
              comments.push({
                path: file.filename,
                position: position,
                body: "💡 Consider using `StringUtils.isEmpty(email)` for better null/empty checks."
              });
              console.log(`Adding comment at position ${position}: ${line}`);
            }
          }
        });
      }
    }

    console.log(`Generated ${comments.length} review comments`);

    if (comments.length > 0) {
      console.log('Review comments:', JSON.stringify(comments, null, 2));
      
      try {
        const review = await octokit.pulls.createReview({
          owner,
          repo,
          pull_number,
          commit_id: pullRequest.head.sha,
          body: "## 🤖 Automated Code Review\n\nI've reviewed the changes and found some suggestions for improvement.",
          event: "COMMENT",
          comments: comments
        });
        console.log('Successfully created review:', review.data.id);
      } catch (error) {
        console.error('Error creating review:', {
          status: error.status,
          message: error.message,
          errors: error.response?.data?.errors
        });
        throw error;
      }
    } else {
      await octokit.pulls.createReview({
        owner,
        repo,
        pull_number,
        commit_id: pullRequest.head.sha,
        body: "## ✅ All Good!\n\nNo issues found in this PR. Great work!",
        event: "APPROVE"
      });
      console.log('Approved PR - no issues found');
    }
  } catch (error) {
    console.error('Error in PR review:', error);
    throw error;
  }
}

async function main() {
  try {
    console.log('Starting PR Review Bot...');
    console.log('Environment variables:', {
      appId: process.env.APP_ID ? 'Set' : 'Not set',
      privateKey: process.env.PRIVATE_KEY ? 'Set' : 'Not set',
      installationId: process.env.INSTALLATION_ID ? 'Set' : 'Not set'
    });

    const octokit = new Octokit({
      authStrategy: createAppAuth,
      auth: {
        appId: process.env.APP_ID,
        privateKey: process.env.PRIVATE_KEY,
        installationId: process.env.INSTALLATION_ID,
      },
    });

    // Get PR number from environment
    const prNumber = process.env.PR_NUMBER || 
      (process.env.GITHUB_EVENT_PATH ? 
        require(process.env.GITHUB_EVENT_PATH).pull_request?.number : 
        null);
    console.log('PR Number:', prNumber);

    // Get repository info from environment
    const [owner, repo] = (process.env.GITHUB_REPOSITORY || '').split('/');
    console.log('Repository:', { owner, repo });

    if (!prNumber || !owner || !repo) {
      throw new Error('Missing required environment variables');
    }

    console.log('PR review started successfully');
    await reviewPR(octokit, owner, repo, prNumber);
    console.log('PR review completed successfully');
  } catch (error) {
    console.error('Error in main:', error);
    process.exit(1);
  }
}

// Start the application
main().catch(console.error);

// Export for testing
module.exports = { reviewPR };