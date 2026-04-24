<#import "vue-template.ftl" as layout>
<@layout.vueLayout ; section>
  <#if section = "kcData">
    <script>
      window.$kcData = {
        page: "forgot-password",
        realm: {
          name: "${realm.name}",
          displayName: "${realm.displayName!'Auth Platform'}"
        },
        meta: {},
        messages: {
          username: "<#if !realm.loginWithEmailAllowed>${msg('username')?js_string}<#elseif !realm.registrationEmailAsUsername>${msg('usernameOrEmail')?js_string}<#else>${msg('email')?js_string}</#if>",
          doSubmit: "${msg('doSubmit')?js_string}",
        },
        errors: {
          hasError: <#if messagesPerField.existsError('username')>true<#else>false</#if>,
          username: "<#if messagesPerField.existsError('username')>${messagesPerField.get('username')?js_string}</#if>"
        },
        urls: {
          resetPasswordAction: "${url.loginAction?js_string}",
          loginUrl: "${url.loginUrl?js_string}"
        },
        resourcePath: "${url.resourcesPath}/vue/"
      };
    </script>
  </#if>
</@layout.vueLayout>
