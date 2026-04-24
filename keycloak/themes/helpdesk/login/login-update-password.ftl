<#import "vue-template.ftl" as layout>
<@layout.vueLayout ; section>
  <#if section = "kcData">
    <script>
      window.$kcData = {
        page: "update-password",
        realm: {
          name: "${realm.name}",
          displayName: "${realm.displayName!'Auth Platform'}"
        },
        meta: {},
        messages: {
          passwordNew: "${msg('passwordNew')?js_string}",
          passwordConfirm: "${msg('passwordConfirm')?js_string}",
          doSubmit: "${msg('doSubmit')?js_string}"
        },
        errors: {
          hasError: <#if messagesPerField.existsError('password','password-confirm')>true<#else>false</#if>,
          password: "<#if messagesPerField.existsError('password')>${messagesPerField.get('password')?js_string}</#if>",
          passwordConfirm: "<#if messagesPerField.existsError('password-confirm')>${messagesPerField.get('password-confirm')?js_string}</#if>"
        },
        urls: {
          updatePasswordAction: "${url.loginAction?js_string}"
        },
        resourcePath: "${url.resourcesPath}/vue/"
      };
    </script>
  </#if>
</@layout.vueLayout>
