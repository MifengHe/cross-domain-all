(function () {

    var store = [];

    function CrossDomainEventBus() {}

    CrossDomainEventBus.prototype.dispatch = function(action, payload) {
        var hasAction = false;

        for (var i = 0; i < store.length; i++) {
            var s = store[i];
            if (!s) continue;
            if (s.action !== action) continue;
            if ('once' === s.type) store[i] = null;
            try {
                hasAction = true;
                var callback = s.callback;
                callback(payload);
            } catch (e) {
                if (console && console.error) console.error(e);
            }
        }

        if (hasAction) {
            var store_new = [];
            for (var i = 0; i < store.length; i++) {
                if (!store[i]) continue;
                store_new.push(store[i]);
            }
            store = store_new;
        }

        if (!hasAction) throw new Error("don't have action \""+action+"\"");
    };

    CrossDomainEventBus.prototype.once = function(action, onAction) {
        if (typeof action == 'function') onAction = action, action = "action_" + new Date().getTime() + Math.random();
        if (typeof action != 'string' || action.length == 0) throw new Error('action must be string or no input');
        if (typeof onAction != 'function') throw new Error('onAction must be function');
        store.push({ type: 'once', action: action, callback: onAction });
        return action;
    };

    CrossDomainEventBus.prototype.action = function(action, onAction) {
        if (typeof action == 'function') onAction = action, action = "action_" + new Date().getTime() + Math.random();
        if (typeof action != 'string' || action.length == 0) throw new Error('action must be string or no input');
        if (typeof onAction != 'function') throw new Error('onAction must be function');
        store.push({ type: 'always', action: action, callback: onAction });
        return action;
    };


    var crossDomainEventBus = window.crossDomainEventBus = new CrossDomainEventBus();


    String.prototype._format = function () {
        if (arguments.length == 0) return this;
        var param = arguments[0];
        var s = this;
        if (typeof(param) == 'object') {
            for (var key in param)
                s = s.replace(new RegExp("\\{" + key + "\\}", "g"), param[key]);
            return s;
        } else {
            for (var i = 0; i < arguments.length; i++)
                s = s.replace(new RegExp("\\{" + i + "\\}", "g"), arguments[i]);
            return s;
        }
    };

    var _template =
        '<iframe id="{0}_frame" name="{0}_frame"></iframe>' +
        '<form id="{0}_form"  method="POST" target="{0}_frame" action="{1}">' +
        '   <input id="{0}_sub_name" type="hidden" name="sub_name" value="{2}" />' +
        '   <input id="{0}_action" type="hidden" name="action" value="{3}" />' +
        '   <input id="{0}_data" type="hidden" name="data" value="{4}" />' +
        '   <input id="{0}_cb_action" type="hidden" name="cb_action" value="{5}" />' +
        '</form>';

    function CrossDomainMain(sub_name, url) {
        if (typeof sub_name != 'string' && sub_name.length == 0) throw new Error('sub_name must be string');
        if (typeof url != 'string' && url.length == 0) throw new Error('url must be string');

        var div = document.createElement("div");
        div.id = '_' + new Date().getTime() + Math.random();
        div.style.display = "none";
        div.innerHTML = _template._format(div.id, url, sub_name);

        document.getElementsByTagName('body')[0].appendChild(div);

        this.get = function (key) {
            return document.getElementById(div.id + '_' + key);
        };

    }

    CrossDomainMain.prototype.dispatch = function (action, data, callback) {
        if (typeof action != 'string' && action.length == 0) throw new Error('action must be string');
        if (data == null || typeof data == 'undefined' || (typeof data == "number" && isNaN(data))) data = "";
        data = JSON.stringify(data);

        var cb_action = typeof callback == "function" ? this.once(callback) : "";
        this.get('action').value = action;
        this.get('data').value = data;
        this.get('cb_action').value = cb_action;
        this.get('form').submit();
    };

    CrossDomainMain.prototype.once = function(action, onAction) {
        var _sub = this;
        return crossDomainEventBus.once(action, function (payload) {
            var callback = function (data) {
                if (!payload.cb_action) return;
                _sub.dispatch(payload.cb_action, data);
            };

            callback(onAction(payload));
        });
    };

    CrossDomainMain.prototype.action = function(action, onAction) {
        var _sub = this;
        return crossDomainEventBus.action(action, function (payload) {
            var callback = function (data) {
                if (!payload.cb_action) return;
                _sub.dispatch(payload.cb_action, data);
            };

            callback(onAction(payload));
        });
    };

    window.CrossDomainMain = CrossDomainMain;
})();