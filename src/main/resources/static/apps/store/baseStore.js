export default {
    namespaced: true,
    state: {
        overlay: false,
        user: {}
    },
    getters: {
        getOverlay: state => state.overlay,
        getUser: state => state.user
    },
    mutations: {
        setOverlay: (state, overlay) => {
            state.overlay = overlay;
        },
        setUser: (state, user) => {
            state.user = user;
        }
    },
    actions: {
        setOverlay({commit}, overlay) {
            commit('setOverlay', overlay);
        },
        setUser({commit}, user) {
            commit('setUser', user);
        },
        initBaseStore({commit}, data) {
            if(data !== undefined){
                commit('setUser', data['user']);
            }else{
                this._vm.xAjax({
                    url:'/base/users/me'
                }).then(resp=>{
                    commit('setUser', resp);
                })
            }

            //this.$store.dispatch('base/getUser', '112121'); 로 호출가능
            //store 내에서는 this._vm으로 vm 접근 가능
        }
    }
};