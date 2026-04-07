export enum Role {
  User = "user",
  Admin = "admin",
}

export interface AuthUser {
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  roles: (Role | string)[];
}
