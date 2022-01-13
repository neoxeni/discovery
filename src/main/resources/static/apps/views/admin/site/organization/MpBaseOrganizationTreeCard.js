export default {
    name: 'mp-base-organization-tree-card',
    template :`
    <v-card>
        <v-card-title>
            <v-text-field label="검색" v-model="searchText" @keyup.enter="treeSearch" outlined dense hide-details></v-text-field>
            <v-menu bottom left>
                <template v-slot:activator="{ on, attrs }">
                    <v-btn icon small color="grey" v-bind="attrs" v-on="on">
                        <v-icon>mdi-dots-vertical</v-icon>
                    </v-btn>
                </template>
                <v-list>
                    <v-list-item link @click="expandAll">
                        <v-list-item-title>
                            <v-icon>mdi-arrow-expand-all</v-icon> Expand
                        </v-list-item-title>
                    </v-list-item>
                    <v-list-item link @click="collapseAll">
                        <v-list-item-title>
                            <v-icon>mdi-arrow-collapse-all</v-icon> Collapse
                        </v-list-item-title>
                    </v-list-item>
                </v-list>
            </v-menu>
        </v-card-title>
        <v-card-text class="div-scroll-y">
            <mp-base-tree url="/base/organizations/tree" ref="codeTree" @changed="treeChanged" state></mp-base-tree>
        </v-card-text>
    </v-card>
    `,
    props: {
        dnd: {
            type: Boolean,
            default: false
        },
        changed: {
            type: Function,
            default: function (e, data, instance) {
                console.log('changed default function');
            }
        }
    },
    data: function () {
        return {
            searchText: ''
        };
    },
    methods: {
        renameNode(item, name) {
            this.$refs['jstree'].renameNode(item, name);
        },
        selectNode(nodeId) {
            this.$refs['jstree'].selectNode(nodeId);
        },
        treeSearch() {
            this.$refs['jstree'].search(this.searchText);
        },
        expandAll() {
            this.$refs['jstree'].openAll();
        },
        collapseAll() {
            this.$refs['jstree'].closeAll();
        },
        treeLoaded(e, data, instance) {
            instance.jstree().open_node(1);
        },
        treeMoved(e, data, instance) {
            const parentNode = instance.jstree().get_node(data.parent);
            const targetData = data.node.original;
            const parentData = parentNode.original;
            const gubun = targetData['gubun'];
            const ajaxData = {
                moveType: gubun
            };
            if (gubun === 'E') {
                ajaxData['empNo'] = targetData['no'];
                ajaxData['deptNo'] = parentData['no'];
                //console.log('직원 ' + targetData['text'] + ' ' + parentData['text'] + '부서로 이동');
            } else if (gubun === 'D') {
                ajaxData['deptNo'] = targetData['no'];
                ajaxData['pDeptNo'] = parentData['no'];
                //console.log('부서 ' + targetData['text'] + ' ' + parentData['text'] + '부서로 이동');
            }
            xAjax({
                url: '/base/organizations/departments/change',
                method: 'PATCH',
                contentType: 'application/json',
                data: JSON.stringify(ajaxData)
            }).then(resp => {
                mercury.base.lib.notify(resp.message);
                //this.$refs['jstree'].refresh(this.item.parent);
            });
        }
    }
}

