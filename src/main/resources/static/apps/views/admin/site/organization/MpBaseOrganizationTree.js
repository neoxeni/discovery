const organizationHelper = Vuex.createNamespacedHelpers('base');
export default {
    name: 'mp-base-organization-tree',
    template: `
        <div ref="org-jstree" style="background: #fff;"></div>
    `,
    props: {
        changed: Function,
        created: Function,
        renamed: Function,
        deleted: Function,
        moved: Function,
        onlyUse: {
            type: Boolean,
            default: true
        },
        multiple: {
            type: Boolean,
            default: false
        },
        dnd: {
            type: Boolean,
            default: false
        },
        checkbox: {
            type: Boolean,
            default: false
        },
        selectedDepartment: {
            type: Object,
            default: null
        }
    },
    data: function () {
        return {
            instance: undefined
        };
    },
    async mounted() {
        const options = {
            multiple: this.multiple,
            dnd: this.dnd,
            checkbox: this.checkbox
        };

        const plugins = ['themes', 'json_data', 'ui', 'crrm', 'types', 'search'/*, "wholerow"*//*, "contextmenu"*/];
        if (options['dnd']) {
            plugins.push('dnd');
        }

        if (options['checkbox']) {
            plugins.push('checkbox');
        }


        if(this.getOrganizationTree == null) {
            await this.setOrganizationTree();
        }

        const treeData = _.cloneDeep(this.onlyUse ? this.getOrganizationTree : this.getOrganizationTreeAll);
        const listData = this.onlyUse ? this.getCommonSelectTableData.list : this.getCommonSelectTableDataAll.list

        this.convertJsTreeData(treeData, true, null);

        const _this = this;
        const $refTree = $(this.$refs['org-jstree']);
        const instance = this.instance = $refTree.jstree({
            'plugins': plugins,
            'themes': {
                'responsive': false
            },
            core: {
                multiple: options.multiple,
                data: treeData,
                check_callback: function (operation, node, node_parent, node_position, more) {
                    /*
                    * operation : 동작 상태('create_node', 'rename_node', 'delete_node', 'move_node', 'copy_node' or 'edit')
                    * node : 선택된 노드 정
                    * node_parent : Drop 된 트리의 부모 노드 정보
                    * node_position : Drop 된 위치
                    * more : 기타 정보
                    */

                    const parentData = node_parent.original;
                    const currentData = node.original;

                    //console.log(parentData, currentData);

                    if (operation === 'move_node') {
                        if (parentData !== undefined) {
                            if (parentData.gubun === 'E') {
                                return false;//직원 위로 옮기려 한 경우
                            }
                        }
                    }

                    return true;
                }
            },
            'state': {
                'key': 'mercury-base-organization'
            },
            'types': {
                'default': {
                    'icon': 'mdi mdi-microsoft-teams text-primary'
                },
                'file': {
                    'icon': 'mdi mdi-account  text-secondary'
                }
            },
            search: {
                case_insensitive: true,
                show_only_matches: true,
                search_callback: function (str, node) {
                    if (_this.selectedDepartment != null && _this.selectedDepartment.id !== 'DEPT_ALL') {
                        const selectedDepartmentId = _this.selectedDepartment.id;
                        const target = node.parents.concat(...node.id);
                        if (target.indexOf(selectedDepartmentId) == -1) {
                            return false;
                        }
                    }


                    if (str === null || str === '' || str === '%%%') {
                        return true;
                    }


                    if (str.indexOf('/') === 0) {//경로 검색
                        const path = node.data.path + node.data.paths;
                        return path.includes(str);
                    }

                    const text = node.text.toLowerCase();
                    return text.includes(str.toLowerCase());
                }
            }
        }).on('loaded.jstree', function (e, data) {
            _this.$emit('loaded', e, data, $refTree, listData);
        }).on('changed.jstree', function (e, data) {
            _this.$emit('changed', e, data, $refTree);
        }).on('create_node.jstree', function (e, data) {
            _this.$emit('created', e, data, $refTree);
        }).on('rename_node.jstree', function (e, data) {
            _this.$emit('renamed', e, data, $refTree);
        }).on('delete_node.jstree', function (e, data) {
            _this.$emit('deleted', e, data, $refTree);
        }).on('move_node.jstree', function (e, data) {
            _this.$emit('moved', e, data, $refTree);
        }).on('search.jstree', function (nodes, str, res) {
            // 검색결과가 0개이면 모두 숨김처리한다.
            // 아래 처리를 하지 않으면 모두 보여지게됨.
            if (str.nodes.length === 0) {
                $(_this.$refs['org-jstree']).jstree(true).hide_all();
            }
        });
    },
    computed: {
        ...organizationHelper.mapGetters([
            'getOrganizationTree',
            'getOrganizationTreeAll',
            'getCommonSelectTableData',
            'getCommonSelectTableDataAll'])
    },
    watch: {
        selectedDepartment: {
            handler(selectedDepartment) {
                console.log('selectedDepartment', selectedDepartment);
                this.search(null);
            }
        },
        onlyUse: {
            handler(){
                this.reset();
            }
        }
    },
    methods: {
        ...organizationHelper.mapActions(['setOrganizationTree']),
        $jstree() {
            return $(this.$refs['org-jstree']);
        },
        async reset(){
            await this.setOrganizationTree();

            const treeInstance = $(this.$refs['org-jstree']).jstree(true);
            const treeData = _.cloneDeep(this.onlyUse ? this.getOrganizationTree : this.getOrganizationTreeAll);
            this.convertJsTreeData(treeData, true, null);

            treeInstance.clear_search();

            treeInstance.settings.core.data = treeData;
            treeInstance.refresh();

            const listData = this.onlyUse ? this.getCommonSelectTableData.list : this.getCommonSelectTableDataAll.list;
            this.$emit('loaded', null, treeData, $(this.$refs['org-jstree']), listData);
        },
        refresh(nodeId) {
            if (nodeId !== undefined) {
                $(this.$refs['org-jstree']).jstree(true).refresh_node(nodeId);
            } else {
                $(this.$refs['org-jstree']).jstree(true).refresh();
            }
        },
        search(searchText) {
            const $jstree = $(this.$refs['org-jstree']).jstree(true);
            $jstree.show_all();
            $jstree.search(searchText || '%%%');
        },
        open_node(node) {
            $(this.$refs['org-jstree']).jstree(true).open_node(node);
        },
        openAll() {
            $(this.$refs['org-jstree']).jstree(true).open_all();
        },
        closeAll() {
            $(this.$refs['org-jstree']).jstree(true).close_all();
        },
        showAll() {
            $(this.$refs['org-jstree']).jstree(true).show_all();
        },
        renameNode(node, text) {
            $(this.$refs['org-jstree']).jstree(true).rename_node(node, text);
        },
        rename_node(node, text) {
            $(this.$refs['org-jstree']).jstree(true).rename_node(node, text);
        },
        selectNode(nodeId) {
            $(this.$refs['org-jstree']).jstree(true).deselect_all();
            $(this.$refs['org-jstree']).jstree(true).select_node(nodeId);
        },
        getJson(arg1, arg2) {
            return $(this.$refs['org-jstree']).jstree(true).get_json(arg1, arg2);
        },
        checkNode(idArray, bool = true) {
            if (!this.checkbox) {
                return;
            }

            const $jstree = $(this.$refs['org-jstree']);
            if (bool) {
                if(idArray === 'all') {
                    $jstree.jstree(true).check_all(true);
                }else {
                    $jstree.jstree(true).check_node(idArray);
                }
            } else {
                if(idArray === 'all') {
                    $jstree.jstree(true).uncheck_all(true);
                }else {
                    $jstree.jstree(true).uncheck_node(idArray);
                }
            }
        },
        convertJsTreeData(data, root, parent = null) {
            if(data.type == 'E') {
                data.text = `${data.name} (${data.cmpnyEmpCd || '-'})`;
            }else {
                data.text = data.name;
            }

            //data.text = data.name;
            data.gubun = data.type;
            data.id = data.type + data.no;

            if (root) {
                data.icon = 'mdi mdi-office-building mdi-18px';
            } else {
                if (data.gubun === 'D') {
                    data.icon = 'mdi mdi-microsoft-teams mdi-18px text-primary';
                } else {
                    data.icon = 'mdi mdi-account mdi-18px text-secondary';
                }
            }

            if(!data.data) {
                data.data = {};
            }

            const _data = data.data;

            if (parent) {
                const path = [data.text];
                path.push(...parent.data.path);
                _data.path = path;
            } else {
                _data.path = [data.text];
            }

            if (data.children && data.children.length > 0) {
                data.children.forEach(child => this.convertJsTreeData(child, false, data));
            }
        }
    }
};

