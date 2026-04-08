Verify Your Email Address
==========================

Hello<#if user?? && user.firstName??> ${user.firstName}</#if><#if user?? && user.lastName??> ${user.lastName}</#if>,

Thank you for registering with ${realmName!'Helpdesk'}! To complete your registration, please verify your email address by clicking the link below:

${link}

If you cannot click the link, please copy and paste it into your browser.

IMPORTANT: This link will expire in ${linkExpirationHours!'24'} hours.

If you didn't create an account with ${realmName!'Helpdesk'}, please ignore this email.

---
© ${currentYear!'2026'} ${realmName!'Helpdesk'}. All rights reserved.
