export interface KeycloakData {
  page: "login" | "register" | "forgot-password" | "update-password" | "verify-email" | "error";
  realm: {
    name: string;
    displayName: string;
  };
  meta: {
    [key: string]: unknown;
  };
  messages: Record<string, string>;
  errors: {
    hasError: boolean;
    [key: string]: unknown;
  };
  urls: Record<string, string>;
  resourcePath: string;
}

declare global {
  interface Window {
    $kcData?: KeycloakData;
  }
}
