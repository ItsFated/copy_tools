this.log = {
    /** 一行的分隔符 */
    lineSeparator : '\n',

    /** 默认的标签 */
    tag : "Log",

    /** 不带标签的记录方法 */
    l : function(){
	for(let arg in arguments){
	    this.doLog('['+arg+']->'+arguments[arg]);
	}
    },

    /** 带标签的记录方法 */
    log : function(){
	if(arguments.length <=1){
	    this.doLog('[TAG ('+this.tag+') ]-['+0+']->'+arguments[0]);
	} else {
	    this.doLog('[TAG ('+arguments[0]+') ]');
	    for(let i=1; i<arguments.length; i++){
		this.doLog('['+i+']->'+arguments[i]);
	    }
	}
    },
    
    /** doLog(tag[, msg1[, msg2 ...]]) */
    doLog : function(msg){
	console.log(msg);
    }
};

if(typeof $ === 'undefined') this.$.log = new Log();


/** 将实例的所有属性全部变成字符串 */
function objectInfos(obj, separator){
    if (obj === null) return "{ null }";
    if (typeof(obj) === "undefined") return "{ undefined }";
    var infos = "";
    if(typeof(separator) == "undefined") separator = ',';
    for(var field in obj){
	infos += field + ": ["+obj[field]+ ']' + separator;
    }
    if(infos.length <=0) return "{ }";
    return '{ '+ infos.substring(0, infos.length-1) +'} ';
}

function Log(tag, on){
    this.tag = (typeof tag === "undefined") ? "[Log]" : tag;
    this.on = (typeof on === "undefined") ? true : on;
    
}

var t = new Log();
log.l(objectInfos(t));
