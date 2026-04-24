<#import "vue-template.ftl" as layout>
<@layout.vueLayout ; section>
  <#if section = "kcData">
    <script>
      window.$kcData = {
        page: "verify-email",
        realm: {
          name: "${realm.name}",
          displayName: "${realm.displayName!'Auth Platform'}"
        },
        messages: {
          email: "<#if user??>${user.email!?js_string}<#elseif message?has_content>${message.summary?js_string}</#if>",
          doResend: "${msg('doResend')?js_string}",
          backToLogin: "${msg('backToLogin')?js_string}"
        },
        errors: {
          hasError: <#if messagesPerField.existsError('email','userEmail')>true<#else>false</#if>,
          message: "<#if messagesPerField.existsError('email','userEmail')>${messagesPerField.getFirst('email','userEmail')?js_string}</#if>"
        },
        urls: {
          resendEmailAction: "${url.loginAction?js_string}",
          loginUrl: "${url.loginUrl?js_string}"
        },
        resourcePath: "${url.resourcesPath}/vue/"
      };
    </script>
  </#if>
</@layout.vueLayout>
