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
  const { data: files } = await octokit.pulls.listFiles({ owner, repo, pull_number });
  console.log(`Found ${files.length} files to review`);
  
  const comments = [];
  for (const file of files) {
    if (file.filename.endsWith("UserController.java")) {
      const patch = file.patch.split("\n");
      let lineInFile = null;
      let lineInDiff = 0;

      patch.forEach((line) => {
        if (line.startsWith("@@")) {
          const match = line.match(/@@ -\d+(?:,\d+)? \+(\d+)(?:,\d+)? @@/);
          if (match) {
            lineInFile = parseInt(match[1], 10) - 1;
          }
          lineInDiff = -1; // Reset diff counter at each hunk
          return;
        }

        lineInDiff++;
        
        if (lineInFile === null) return;
        
        if (line[0] === ' ' || line[0] === '+') {
          lineInFile++;
        }

        if (line[0] === '+') {
          // Check for issues in added/modified lines
          if (line.includes("throws Exception")) {
            comments.push({
              path: file.filename,
              start_line: lineInFile,
              start_side: 'RIGHT',
              body: "🚨 Avoid using `throws Exception`. Use specific exception types instead to provide better error handling and documentation."
            });
            console.log(`Adding comment at file line ${lineInFile}: ${line}`);
          }
          if (line.includes("email.isEmpty() || email.length() == 0")) {
            comments.push({
              path: file.filename,
              start_line: lineInFile,
              start_side: 'RIGHT',
              body: "💡 Consider using `StringUtils.isEmpty(email)` for better null/empty checks."
            });
            console.log(`Adding comment at file line ${lineInFile}: ${line}`);
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
        body: "## 🤖 Automated Code Review\n\nI've reviewed the changes and found some suggestions for improvement.",
        event: "COMMENT",
        comments
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
      body: "## ✅ All Good!\n\nNo issues found in this PR. Great work!",
      event: "APPROVE"
    });
    console.log('Approved PR - no issues found');
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

    // Parse GitHub event data
    let prNumber;
    if (process.env.GITHUB_EVENT_PATH) {
      const eventData = JSON.parse(fs.readFileSync(process.env.GITHUB_EVENT_PATH, 'utf8'));
      prNumber = eventData.pull_request?.number;
      console.log('Parsed PR number from event:', prNumber);
    } else {
      prNumber = process.env.PR_NUMBER;
      console.log('Using PR number from environment:', prNumber);
    }

    // Get repository info
    const [owner, repo] = (process.env.GITHUB_REPOSITORY || '').split('/');
    console.log('Repository:', { owner, repo });

    if (!prNumber || !owner || !repo) {
      throw new Error(`Missing required data: PR=${prNumber}, owner=${owner}, repo=${repo}`);
    }

    const octokit = new Octokit({
      authStrategy: createAppAuth,
      auth: {
        appId: process.env.APP_ID,
        privateKey: process.env.PRIVATE_KEY,
        installationId: process.env.INSTALLATION_ID,
      },
    });

    console.log(`PR review started for #${prNumber}`);
    await reviewPR(octokit, owner, repo, prNumber);
    console.log('PR review completed successfully');
  } catch (error) {
    console.error('Error in PR review:', error);
    process.exit(1);
  }
}

// Start the application
main().catch(console.error);

// Export for testing
module.exports = { reviewPR };