<#import "vue-template.ftl" as layout>
<@layout.vueLayout ; section>
  <#if section = "kcData">
    <script>
      window.$kcData = {
        page: "register",
        realm: {
          name: "${realm.name}",
          displayName: "${realm.displayName!'Auth Platform'}"
        },
        meta: {
          registrationEmailAsUsername: ${realm.registrationEmailAsUsername?c}
        },
        messages: {
          firstName: "${msg('firstName')?js_string}",
          lastName: "${msg('lastName')?js_string}",
          email: "${msg('email')?js_string}",
          username: "${msg('username')?js_string}",
          password: "${msg('password')?js_string}",
          passwordConfirm: "${msg('passwordConfirm')?js_string}",
          doRegister: "${msg('doRegister')?js_string}",
        },
        errors: {
          hasError: <#if messagesPerField.existsError('firstName','lastName','email','username','password','password-confirm')>true<#else>false</#if>,
          firstName: "<#if messagesPerField.existsError('firstName')>${messagesPerField.get('firstName')?js_string}</#if>",
          lastName: "<#if messagesPerField.existsError('lastName')>${messagesPerField.get('lastName')?js_string}</#if>",
          email: "<#if messagesPerField.existsError('email')>${messagesPerField.get('email')?js_string}</#if>",
          username: "<#if messagesPerField.existsError('username')>${messagesPerField.get('username')?js_string}</#if>",
          password: "<#if messagesPerField.existsError('password')>${messagesPerField.get('password')?js_string}</#if>",
          passwordConfirm: "<#if messagesPerField.existsError('password-confirm')>${messagesPerField.get('password-confirm')?js_string}</#if>"
        },
        urls: {
          registerAction: "${url.registrationAction?js_string}",
          loginUrl: "${url.loginUrl?js_string}"
        },
        resourcePath: "${url.resourcesPath}/vue/"
      };
    </script>
  </#if>
</@layout.vueLayout>
