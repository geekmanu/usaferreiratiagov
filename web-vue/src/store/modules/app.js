/**
 * 应用相关的 store
 * 存储所有打开的 tab 窗口，并且记录当前激活的 tab
 * 另外提供打开新 tab、跳转 tab、移除 tab 功能
 */
import { ACTIVE_TAB_KEY, TAB_LIST_KEY, ACTIVE_MENU_KEY, CACHE_WORKSPACE_ID } from "@/utils/const";

const app = {
  state: {
    activeTabKey: localStorage.getItem(ACTIVE_TAB_KEY),
    tabList: JSON.parse(localStorage.getItem(TAB_LIST_KEY)),
    activeMenuKey: localStorage.getItem(ACTIVE_MENU_KEY),
    workspaceId: localStorage.getItem(CACHE_WORKSPACE_ID),
    // 菜单折叠
    collapsed: localStorage.getItem("collapsed"),
    menuOpenKeys: [],
  },
  mutations: {
    setActiveTabKey(state, activeKey) {
      state.activeTabKey = activeKey;
    },
    setTabList(state, tabList) {
      state.tabList = tabList;
    },
    setActiveMenuKey(state, activeMenuKey) {
      state.activeMenuKey = activeMenuKey;
    },
    setWorkspace(state, workspaceId) {
      state.workspaceId = workspaceId;
    },
    setCollapsed(state, collapsed) {
      state.collapsed = collapsed;
    },
    setMenuOpenKeys(state, keys) {
      state.menuOpenKeys = keys;
    },
  },
  actions: {
    // 添加 tab
    addTab({ commit, state, rootGetters, dispatch }, tab) {
      return new Promise((resolve) => {
        // 从 store 里面拿到 menus 匹配 path 得到当前的菜单，设置 tab 的标题
        const menus = rootGetters.getMenus;
        let currentMenu = null;
        menus.forEach((menu) => {
          menu.childs.forEach((subMenu) => {
            if (subMenu.path === tab.path) {
              currentMenu = subMenu;
              currentMenu.parent = menu;
            }
          });
        });
        if (!currentMenu) {
          return;
        }
        tab.title = currentMenu.title;
        tab.id = currentMenu.id;
        tab.parentId = currentMenu.parent.id;
        let tabList = state.tabList || [];
        // 获取下标 -1 表示可以添加 否则就是已经存在
        const index = tabList.findIndex((ele) => ele.key === tab.key);
        if (index > -1) {
          // 设置 activeTabKey
        } else {
          // 新增
          tabList.push(tab);
          commit("setTabList", tabList);
          localStorage.setItem(TAB_LIST_KEY, JSON.stringify(tabList));
        }
        // 设置当前选择的菜单
        dispatch("activeTabKey", tab.key);
        dispatch("activeMenu", tab.id);
        resolve();
      });
    },
    // 删除 tab
    removeTab({ commit, state, dispatch }, key) {
      return new Promise((resolve) => {
        let tabList = state.tabList;
        const index = tabList.findIndex((ele) => ele.key === key);
        tabList.splice(index, 1);

        commit("setTabList", tabList);
        localStorage.setItem(TAB_LIST_KEY, JSON.stringify(tabList));
        // 如果删除的是 activeTabKey
        if (state.activeTabKey === key) {
          // 寻找下一个
          const tempTab = tabList[Math.min(index, 0)];
          // 如果还是原来激活的 tab 就不用更新
          if (state.activeTabKey !== tempTab.key) {
            dispatch("activeTabKey", tempTab.key);
            resolve();
          }
        }
      });
    },
    // 清除 tabs
    clearTabs({ commit, state, dispatch }, { key, position }) {
      return new Promise((resolve) => {
        let tabList = state.tabList;
        key = key || state.activeTabKey;

        // 找到当前 index
        const index = tabList.findIndex((ele) => ele.key === key);
        const currentTab = tabList[index];
        //console.log(index, key, state.activeTabKey, position);
        if (position === "left") {
          // 关闭左侧
          tabList = tabList.slice(index, tabList.length);
        } else if (position === "right") {
          // 关闭右侧
          tabList = tabList.slice(0, index + 1);
        } else {
          // 只保留当前
          tabList = [currentTab];
        }
        commit("setTabList", tabList);
        localStorage.setItem(TAB_LIST_KEY, JSON.stringify(tabList));
        //
        if (state.activeTabKey !== key) {
          dispatch("activeTabKey", key);
          resolve(key);
        }
      });
    },
    activeTabKey({ commit }, key) {
      commit("setActiveTabKey", key);
      localStorage.setItem(ACTIVE_TAB_KEY, key);
    },
    // 选中当前菜单
    activeMenu({ commit }, activeMenuKey) {
      commit("setActiveMenuKey", activeMenuKey);
      localStorage.setItem(ACTIVE_MENU_KEY, activeMenuKey);
    },
    // 切换工作空间
    changeWorkspace({ commit }, workspaceId) {
      commit("setWorkspace", workspaceId);
      localStorage.setItem(CACHE_WORKSPACE_ID, workspaceId);
    },
    collapsed({ commit }, collapsed) {
      commit("setCollapsed", collapsed);
      localStorage.setItem("collapsed", collapsed);
    },
    // 打开的菜单
    menuOpenKeys({ commit, state }, keys) {
      if (Array.isArray(keys)) {
        commit("setMenuOpenKeys", keys);
      } else if (typeof keys == "string") {
        const nowKeys = state.menuOpenKeys;
        if (!nowKeys.includes(keys)) {
          nowKeys.push(keys);
          commit("setMenuOpenKeys", nowKeys);
        }
      }
    },
  },
  getters: {
    getTabList(state) {
      return state.tabList;
    },
    getActiveTabKey(state) {
      return state.activeTabKey;
    },
    getActiveMenuKey(state) {
      return state.activeMenuKey;
    },
    getWorkspaceId(state) {
      return state.workspaceId;
    },
    getCollapsed(state) {
      if (state.collapsed === undefined || state.collapsed === null) {
        return 0;
      }
      return parseInt(state.collapsed);
    },
    getMenuOpenKeys(state) {
      return state.menuOpenKeys;
    },
  },
};

export default app;
