define(["i18n"], function(i18n) {
	Date.prototype.setISO8601 = function(str) {
		var regexp = "([0-9]{4})(-([0-9]{2})(-([0-9]{2})" + "(T([0-9]{2}):([0-9]{2})(:([0-9]{2})(\\.([0-9]+))?)?"
				+ "(Z|(([-+])([0-9]{2}):([0-9]{2})))?)?)?)?";
		var d = str.match(new RegExp(regexp));
		if (d) {
			var date = new Date(d[1], 0, 1), offset = 0, time;

			if (d[3]) {
				date.setMonth(d[3] - 1);
			}
			if (d[5]) {
				date.setDate(d[5]);
			}
			if (d[7]) {
				date.setHours(d[7]);
			}
			if (d[8]) {
				date.setMinutes(d[8]);
			}
			if (d[10]) {
				date.setSeconds(d[10]);
			}
			if (d[12]) {
				date.setMilliseconds(Number("0." + d[12]) * 1000);
			}
			if (d[14]) {
				offset = (Number(d[16]) * 60) + Number(d[17]);
				offset *= ((d[15] === '-') ? 1 : -1);
			}

			time = (Number(date) + (offset * 60 * 1000));

			this.setTime(Number(time));
			return true;
		} else {
			return false;
		}
	};
	
	Date.prototype.toISO8601String = function() {
		function pad(n) {
			return n < 10 ? '0' + n : n;
		}
		return this.getFullYear() + '-'//
				+ pad(this.getMonth() + 1) + '-'//
				+ pad(this.getDate()) + 'T'//
				+ pad(this.getHours()) + ':'//
				+ pad(this.getMinutes()) + ':'//
				+ pad(this.getSeconds()) + '.'//
				+ pad(this.getMilliseconds()) + 'Z';
	};

	Date.prototype.getLocalizedDate = function() {
		var date = this.toISO8601String();
		var defaultMonths = [ "Jan.", "Feb.", "Mar.", "Apr.", "May", "June", "July", "Aug.", "Sep.", "Oct.", "Nov.", "Dec." ];
		var months = i18n.months ? eval(i18n.months) : defaultMonths;
		var arr = date.split("-");

		if (arr[1]) {
			arr[1] = months[arr[1] - 1];
		}

		return arr[1] + " " + arr[0];
	};

	Date.getLocalizedDate = function(dateString) {
		var tmpDate = new Date();
		tmpDate.setISO8601(dateString);
		return tmpDate.getLocalizedDate();
	};

});