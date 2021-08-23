import { createStore } from 'vuex'
declare let SessionStorage: any;
const USER = "USER";
const store = createStore({
  state: {
    user: SessionStorage.get(USER) || {} //这样写避免空指针异常
  },
  mutations: {
    setUser (state, user){
      state.user = user;
      SessionStorage.set(USER, user);
    }
  },
  actions: {
  },
  modules: {
  }
});

export default store;
