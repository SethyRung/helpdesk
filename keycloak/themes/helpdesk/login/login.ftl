<#import "vue-template.ftl" as layout>
<@layout.vueLayout ; section>
  <#if section = "kcData">
    <script>
      window.$kcData = {
        page: "login",
        realm: {
          name: "${realm.name}",
          displayName: "${realm.displayName!'Auth Platform'}"
        },
        meta: {
          rememberMe: ${realm.rememberMe?c},
          resetPasswordAllowed: ${(realm.password && realm.resetPasswordAllowed)?c},
          registrationAllowed: ${(realm.password && realm.registrationAllowed && !registrationDisabled??)?c}
        },
        messages: {
          username: "<#if !realm.loginWithEmailAllowed>${msg('username')?js_string}<#elseif !realm.registrationEmailAsUsername>${msg('usernameOrEmail')?js_string}<#else>${msg('email')?js_string}</#if>",
          password: "${msg('password')?js_string}",
          rememberMe: "${msg('rememberMe')?js_string}",
          doLogin: "${msg('doLogIn')?js_string}",
        },
        errors: {
          hasError: <#if messagesPerField.existsError('username','password')>true<#else>false</#if>,
          username: "<#if messagesPerField.existsError('username')>${messagesPerField.get('username')?js_string}</#if>",
          password: "<#if messagesPerField.existsError('password')>${messagesPerField.get('password')?js_string}</#if>"
        },
        urls: {
          loginAction: "${url.loginAction?js_string}",
          forgotPassword: "${url.loginResetCredentialsUrl?js_string}",
          register: "${url.registrationUrl?js_string}"
        },
        resourcePath: "${url.resourcesPath}/vue/"
      };
    </script>
  </#if>
</@layout.vueLayout>
