<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width">
    <title>Verify Your Email</title>
</head>
<body style="margin: 0;padding: 0;font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;background: #f9fafb;">
    <div class="email-container" style="max-width: 600px;margin: 0 auto;padding: 20px;">
        <table role="presentation" width="100%" bgcolor="#ffffff" style="border-radius: 12px;overflow: hidden;box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
            <tr><td class="email-header" style="text-align: center;padding: 30px 0;border-bottom: 1px solid #e5e7eb;">
                <div class="logo" style="font-size: 24px;font-weight: 600;color: #1f2937;">${realmName!'Helpdesk'}</div>
            </td></tr>
            <tr><td class="email-body" style="padding: 40px 20px;">
                <h1 class="heading" style="font-size: 24px;font-weight: 600;color: #111827;margin: 0 0 16px 0;">Verify Your Email Address</h1>
                <p class="greeting" style="font-size: 16px;color: #374151;margin-bottom: 16px;">Hello<#if user?? && user.firstName??> ${user.firstName}</#if>,</p>
                <p class="message" style="font-size: 15px;line-height: 1.6;color: #4b5563;margin-bottom: 24px;">
                    Thank you for registering with <strong>${realmName!'Helpdesk'}</strong>! To complete your registration, please verify your email address.
                </p>
                <div class="button-container" style="text-align: center;margin: 32px 0;">
                    <a href="${link}" class="button-link" style="display: inline-block;padding: 14px 32px;background: #22c55e;color: #ffffff;text-decoration: none;border-radius: 8px;font-weight: 500;font-size: 15px;" target="_blank">Verify Email Address</a>
                </div>
                <div class="info-box" style="background: #f9fafb;border-left: 4px solid #f59e0b;padding: 16px;margin: 24px 0;border-radius: 4px;">
                    <p class="info-box-text" style="font-size: 14px;color: #92400e;margin: 0;">
                        <strong>This link will expire in ${linkExpirationHours!'24'} hours.</strong><br>If you didn't create an account, please ignore this email.
                    </p>
                </div>
            </td></tr>
            <tr><td class="email-footer" style="text-align: center;padding: 20px;color: #6b7280;font-size: 14px;border-top: 1px solid #e5e7eb;">
                <p style="margin: 0;">&copy; ${currentYear!'2026'} ${realmName!'Helpdesk'}</p>
            </td></tr>
        </table>
    </div>
</body>
</html>
