
export default {
    name: 'mp-base-group-combo',
    template: `
    <v-combobox v-model="selectedItemsInner" :items="items" :label="label" item-text="name" item-value="code" multiple :clearable="!readonly" :readonly="readonly" return-object @change="changeItems">
        <template v-slot:item="data">
            <v-chip class="ma-2" x-small label>{{ data.item.code }}</v-chip> {{ data.item.name }}
        </template>
        <template v-slot:selection="data">
            <v-menu bottom right transition="scale-transition" origin="top left">
                <template v-slot:activator="{ on }">
                    <v-chip v-bind="data.attrs" v-on="on" pill :close="!readonly" @click:close="removeItem(data.item)">
                        <v-avatar class="accent white--text" left v-text="data.item.code.slice(0, 1).toUpperCase()"></v-avatar>
                        {{ data.item.name }}
                    </v-chip>
                </template>
                <v-card width="300">
                    <v-list dark>
                        <v-list-item>
                            <v-list-item-avatar v-text="data.item.code.slice(0, 1).toUpperCase()">

                            </v-list-item-avatar>
                            <v-list-item-content>
                                <v-list-item-title>{{ data.item.name }}</v-list-item-title>
                                <v-list-item-subtitle>{{ data.item.code }}</v-list-item-subtitle>
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
            const index = this.selectedItemsInner.findIndex(role => role.code === item.code);
            if (index >= 0) {
                this.selectedItemsInner.splice(index, 1);
            }
            this.$emit('update:selectedItems', this.selectedItemsInner);
        }
    }
}

