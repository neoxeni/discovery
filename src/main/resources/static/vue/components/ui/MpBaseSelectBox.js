const MpBaseSelectBox = {
    name: 'mp-base-select-box',
    template: `
        <v-select v-model="innerValue" :items="items" :label="label" multiple 
                  :clearable="!readonly" :readonly="readonly">
            <template v-slot:item="data">
                <v-chip class="ma-2" x-small label>{{ data.item.value }}</v-chip> {{ data.item.text }}
            </template>
            <template v-slot:selection="data">
                <v-menu bottom right transition="scale-transition" origin="top left">
                    <template v-slot:no-data>
                        <v-list-item>
                            <v-list-item-content>
                                <v-list-item-title>
                                    No results matching "<strong>{{ search }}</strong>".
                                </v-list-item-title>
                            </v-list-item-content>
                        </v-list-item>
                    </template>
                    <template v-slot:activator="{ on }">
                        <v-chip pill small :input-value="data.selected">
    <!--                        <v-avatar class="accent white&#45;&#45;text" left v-text="data.item.value"></v-avatar>-->
                            <span v-bind="data.attrs" v-on="on" :class="readonly ? '': 'pr-2'" :title="data.item.value">{{ data.item.text }}</span>
                            <v-icon small @click.prevent.stop="data.parent.selectItem(data.item)" v-show="!readonly">mdi-close-circle</v-icon>
                        </v-chip>
                    </template>
                    <v-card width="300">
                        <v-list dark>
                            <v-list-item>
    <!--                            <v-list-item-avatar v-text="data.item.value"></v-list-item-avatar>-->
                                <v-list-item-content>
                                    <v-list-item-title>{{ data.item.text }}</v-list-item-title>
                                    <v-list-item-subtitle>{{ data.item.value }}</v-list-item-subtitle>
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
        </v-select>
    
    `,
    props: {
        items: Array,
        label: String,
        readonly: {
            type: Boolean,
            default: false
        },
        value: Array
    },
    data: function () {
        return {
            innerValue : this.value,
            search: null,
        };
    },
    watch: {
        innerValue(newVal){
            this.$emit("input", newVal); //value update
        }
    },
    methods: {

    }
};

export default MpBaseSelectBox;