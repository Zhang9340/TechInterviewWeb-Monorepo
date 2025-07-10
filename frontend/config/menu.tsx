import { MenuDataItem } from "@ant-design/pro-layout";
import { CrownOutlined } from "@ant-design/icons";
import AccessEnum from "@/access/accessEnum";
import checkAccess from "@/access/checkAccess";

// menu List
const menus = [
  {
    path: "/",
    name: "HomePage",
  },
  {
    path: "/banks",
    name: "Question Banks",
  },
  {
    path: "/questions",
    name: "Question",
  },
  {
    path: "/admin",
    name: "Admin",
    icon: <CrownOutlined />,
    access: AccessEnum.ADMIN,
    children: [
      {
        path: "/admin/user",
        name: "UserAdmin",
        access: AccessEnum.ADMIN,
      },
      {
        path: "/admin/bank",
        name: "BankAdmin",
        access: AccessEnum.ADMIN,
      },
      {
        path: "/admin/question",
        name: "QuestionAdmin",
        access: AccessEnum.ADMIN,
      },
    ],
  },
] as MenuDataItem[];

export const findAllMenuItemByPath = (path: string): MenuDataItem | null => {
  return findMenuItemByPath(menus, path);
};

export const findMenuItemByPath = (
  menus: MenuDataItem[],
  path: string,
): MenuDataItem | null => {
  for (const menu of menus) {
    if (menu.path === path) {
      return menu;
    }
    if (menu.children) {
      const matchedMenuItem = findMenuItemByPath(menu.children, path);
      if (matchedMenuItem) {
        return matchedMenuItem;
      }
    }
  }
  return null;
};
export default menus;
