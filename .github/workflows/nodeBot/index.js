const { Octokit } = require("@octokit/rest");
const { createAppAuth } = require("@octokit/auth-app");

const appId = process.env.APP_ID;
const privateKey = process.env.PRIVATE_KEY;
const installationId = process.env.INSTALLATION_ID;

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