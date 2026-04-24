<#import "vue-template.ftl" as layout>
<@layout.vueLayout ; section>
  <#if section = "kcData">
    <script>
      window.$kcData = {
        page: "error",
        realm: {
          name: "${realm.name}",
          displayName: "${realm.displayName!'Auth Platform'}"
        },
        meta: {},
        messages: {
          backToLogin: "${msg('backToLogin')?js_string}"
        },
        errors: {
          hasError: true,
          message: <#if message?has_content && message.summary?has_content>"${message.summary?js_string}"<#elseif message?has_content && message.description?has_content>"${message.description?js_string}"<#else>"Something went wrong. Please try again."</#if>,
        },
        urls: {
          loginUrl: "${url.loginUrl?js_string}"
        },
        resourcePath: "${url.resourcesPath}/vue/"
      };
    </script>
  </#if>
</@layout.vueLayout>
