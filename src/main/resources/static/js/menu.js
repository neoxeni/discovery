const templateMenus = {
    id: "LittleJoe",
	name: "LittleJoe",
	path: "/",
	attributes: {},
	children: [
	    {
            id: "Advertiser-Root",
	        name: "Advertiser",
            path: "/client/MpClient",
            attributes: {},
            auth: ['Admin', 'Trader', 'Ae'],
            children: [
                {
                    id: "Client",
                    name: "Client",
                    path: "/client/MpClient",
                    auth: ['Admin', 'Trader'],
                    attributes: {},
                    children: []
                },
                {
                    id: "AdItem",
                    name: "AdItem",
                    path: "/client/MpAdItemTable",
                    auth: ['Admin', 'Trader', 'Ae'],
                    attributes: {},
                    children: [
                        {
                            id: "AdItemTable",
                            name: "Table",
                            path: "/client/MpAdItemTable",
                            auth: ['Admin', 'Trader', 'Ae'],
                            attributes: {},
                            children: []
                        },
                        {
                            id: "AdItemTree",
                            name: "Tree",
                            path: "/client/MpAdItemTree",
                            auth: ['Admin', 'Trader', 'Ae'],
                            attributes: {},
                            children: []
                        },
                        {
                            id: "NameBuilder",
                            name: "Name Builder",
                            path: "/client/MpReportUnitNameBuilder",
                            auth: ['Admin', 'Trader','Ae'],
                            attributes: {},
                            children: []
                        },
                    ]
                },
                {
                    id: "BatchScheduleMonitor",
                    name: "BatchScheduleMonitor",
                    path: "/client/MpBatchScheduleMonitor",
                    auth: ['Admin', 'Trader'],
                    attributes: {},
                    children: []
                }
            ]
	    },
        {
            id: "Report-Root",
            name: "Report",
            path: "/report/MpReportGroup",
            auth: ['Admin', 'Trader', 'Ae'],
            attributes: {},
            children: [
                {
                    id: "ReportGroup",
                    name: "ReportGroup",
                    path: "/report/MpReportGroup",
                    auth: ['Admin', 'Trader', 'Ae'],
                    attributes: {},
                    children: []
                },
                {
                    id: "MpReportDownload",
                    name: "ReportDownload",
                    path: "/report/MpReportDownload",
                    auth: ['Admin', 'Trader', 'Ae'],
                    attributes: {},
                    children: []
                }
            ]
	    },
        {
            id: "Management-Root",
            name: "Management",
            path: "/management/MpUser",
            auth: ['Admin'],
            attributes: {},
            children: [
                {
                    id: "User",
                    name: "User",
                    path: "/management/MpUser",
                    auth: ['Admin'],
                    attributes: {},
                    children: []
                },
                {
                    id: "Management",
                    name: "Management",
                    path: "/management/MpManagement",
                    auth: ['Admin'],
                    attributes: {},
                    children: []
                },
                {
                    id: "Sample",
                    name: "Sample",
                    path: "/sample/MpSampleChart",
                    auth: ['Admin'],
                    attributes: {},
                    children: [
                        {
                            id: "Chart",
                            name: "Chart",
                            path: "/sample/MpSampleChart",
                            auth: ['Admin'],
                            attributes: {},
                            children: []
                        },
                        {
                            id: "Analytics-Funnel-Client",
                            name: "Funnel Analytics",
                            path: "/analytics/MpAnalyticsAdvertiser",
                            auth: ['Admin'],
                            attributes: {},
                            children: []
                        }
                    ]
                }
            ]
	    },
        {
            id: "Trading-Root",
            name: "Trading",
            path: "/trading/MpUser",
            auth: ['Admin', 'Trader'],
            attributes: {},
            children: [
                {
                    id: "Creative",
                    name: "Creative",
                    path: "/trading/MpCreative",
                    auth: ['Admin', 'Trader'],
                    attributes: {},
                    children: []
                },
            ]
	    }
    ]
};

const getTemplateMenu = (user) => {
    /*const userGroupMap = user.roles.reduce(function(map, obj) {
        map[obj] = true;
        return map;
    }, {});*/

    /*var arr = [
        { key: 'foo', val: 'bar' },
        { key: 'hello', val: 'world' }
    ];

    var result = arr.reduce(function(map, obj) {
        map[obj.key] = obj.val;
        return map;
    }, {});*/

    if(user['is_superuser']){
        return templateMenus;
    }

    excludeMenuByGroup(templateMenus.children, {})
    return templateMenus;
}

function excludeMenuByGroup (menuList, userGroupMap){
    const findFunc = (menu) => {
        const menuAuth = menu.auth;
        if (menuAuth === undefined) {
            return false;
        }

        for (let i = 0, ic = menuAuth.length; i < ic; i++) {
            if (userGroupMap[menuAuth[i]]) {
                return false;
            }
        }

        return true;
    };

    let idx = menuList.findIndex(findFunc);
    while (idx > -1) {
        menuList.splice(idx, 1)
        idx = menuList.findIndex(findFunc);
    }

    for( let j = 0, jc = menuList.length ; j < jc; j++){
        excludeMenuByGroup (menuList[j].children, userGroupMap)
    }
}

const getRoutesByMenu = (menu) => {
    const _routes = [{
        name: 'SKYLAB',
        label: 'SKYLAB',
        path: '/',
        component: () => import(`/static/vue/views/MpSkylabMain.js`),
        meta: {
            title: 'SKYLAB'
        }
    }];
    menuToRoutes(menu, _routes);
    return _routes;
};

const menuToRoutes = (menu, routes) => {
    if (menu.children && menu.children.length > 0) {
        menu.children.forEach(childMenu => {
            menuToRoutes(childMenu, routes);
        });
    } else {
        if (menu.path) {
            const name = menu.path.substring(menu.path.lastIndexOf('/') + 1);
            routes.push({
                name: name,
                label: menu.name,
                path: menu.path,
                component: () => import(`/static/vue/views${menu.path}.js`),
                meta: {
                    title: menu.name,
                    menu: menu
                }
            });
        }
    }
};