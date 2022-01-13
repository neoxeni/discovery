
export default {
    name: 'mp-base-group-combo',
    template: `
    <v-combobox v-model="selectedItemsInner" :items="items" :label="label" item-text="grpNm" item-value="grpCd" multiple :clearable="!readonly" :readonly="readonly" return-object @change="changeItems">
        <template v-slot:item="data">
            <v-chip class="ma-2" x-small label>{{ data.item.grpCd }}</v-chip> {{ data.item.cateNm }} > {{ data.item.grpNm }}
        </template>
        <template v-slot:selection="data">
            <v-menu bottom right transition="scale-transition" origin="top left">
                <template v-slot:activator="{ on }">
                    <v-chip v-bind="data.attrs" v-on="on" pill :close="!readonly" @click:close="removeItem(data.item)">
                        <v-avatar class="accent white--text" left v-text="data.item.grpCd.slice(0, 1).toUpperCase()"></v-avatar>
                        {{ data.item.cateNm }} > {{ data.item.grpNm }}
                    </v-chip>
                </template>
                <v-card width="300">
                    <v-list dark>
                        <v-list-item>
                            <v-list-item-avatar v-text="data.item.grpCd.slice(0, 1).toUpperCase()">

                            </v-list-item-avatar>
                            <v-list-item-content>
                                <v-list-item-title>{{ data.item.cateNm }} > {{ data.item.grpNm }}</v-list-item-title>
                                <v-list-item-subtitle>{{ data.item.grpCd }}</v-list-item-subtitle>
                            </v-list-item-content>
                            <v-list-item-action>
                                <v-btn icon>
                                    <v-icon>mdi-close-circle</v-icon>
                                </v-btn>
                            </v-list-item-action>
                        </v-list-item>
                    </v-list>
                </v-card>
            </v-menu>
        </template>
    </v-combobox>
    `,
    props: {
        items: Array,
        selectedItems: Array,
        label: String,
        readonly: {
            type: Boolean,
            default: false
        }
    },
    data: function () {
        return {
            selectedItemsInner: this.selectedItems
        };
    },
    watch: {
        'selectedItems': {
            handler: 'watchItems',
            deep: true
        }
    },
    methods: {
        watchItems(items) {
            this.selectedItemsInner = items;
        },
        changeItems(item) {
            this.$emit('update:selectedItems', item);
        },
        removeItem(item) {
            const index = this.selectedItemsInner.findIndex(role => role.grpCd === item.grpCd);
            if (index >= 0) {
                this.selectedItemsInner.splice(index, 1);
            }
            this.$emit('update:selectedItems', this.selectedItemsInner);
        }
    }
}

