export default {
    namespaced: true,
    state: {
        overlay: false,
        clients: [],
        users: [],  //나를 제외한 전체 유저
        user: {}
    },
    getters: {
        getOverlay: state => state.overlay,
        getClients: state => state.clients,
        getUsers: state => state.users,
        getUser: state => state.user
    },
    mutations: {
        setOverlay: (state, overlay) => {
            state.overlay = overlay;
        },
        setUser: (state, user) => {
            state.user = user;
        },
        setUsers: (state, users) => {
            state.users = users;
        },
        setClients: (state, clients) => {
            state.clients = clients;
        }
    },
    actions: {
        setOverlay({commit}, overlay) {
            commit('setOverlay', overlay);
        },
        setUser({commit}, user) {
            commit('setUser', user);
        },
        setUsers({commit}, users) {
            commit('setUsers', users);
        },
        setClients({commit}, clients) {
            commit('setClients', clients);
        },
        initBaseStore({commit}, data) {
            if(data !== undefined){
                commit('setUser', data['user']);
                commit('setUsers', data['users']);
                commit('setClients',data['clients']);
            }else{
                this._vm.xAjax({
                    url:'/LittleJoe/common/users'
                }).then(resp=>{
                    commit('setUser', resp.user);
                    commit('setUsers', resp.users);
                    return this._vm.xAjax({
                        url:'/LittleJoe/api/clients/?page=1&page_size=500'
                    })
                }).then(resp=>{
                    const clients = []
                    resp.results.forEach(client=> {
                        clients.push({
                            text: client.name,
                            value: client.id
                        })
                    })

                    commit('setClients', clients);
                })
            }

            //this.$store.dispatch('base/getUser', '112121'); 로 호출가능
            //store 내에서는 this._vm으로 vm 접근 가능
        }
    }
};