import React from 'react'
import { render } from 'react-dom'

export class Information extends React.Component {
	
	constructor(props) {
		super(props);
		this.state = {
				energySumByMonth: [],
				energySumByDay: [],
				energyAvgByDay: [],
		};
	}
	
	componentWillMount() {
		this.updateData();
	}
	
	componentDidMount() {
		this.updateData();
	}
	
	updateData = () => {
		var reactComponent = this;
		$.ajax({
			url: "/api/my/conso/contract/sum/day",
			type: 'get',
			dataType: 'json',
			success: function (data) {
				reactComponent.setState({energySumByDay: data});
	        },
	        error: function (xhr, status, err) {
	            console.error(status, err.toString());
	        }
		});
		
		$.ajax({
			url: "/api/my/conso/contract/sum/month",
			type: 'get',
			dataType: 'json',
			success: function (data) {
				reactComponent.setState({energySumByMonth: data});
	        },
	        error: function (xhr, status, err) {
	            console.error(status, err.toString());
	        }
		});
		$.ajax({
			url: "/api/my/conso/contract/avg/day",
			type: 'get',
			dataType: 'json',
			success: function (data) {
				reactComponent.setState({energyAvgByDay: data});
	        },
	        error: function (xhr, status, err) {
	            console.error(status, err.toString());
	        }
		});
		
	}
	
	maxConsForDaysOverLastMonth = () => {
		
		var energy = this.state.energySumByDay.slice(0);
		
        energy.forEach(function (d) {
        	var date = new Date(d.date);
        	d.month = date.getMonth() + 1;
        	d.day = date.getDate();
        });
        
        var years = this.props.findYears(energy.slice(0));
		var lastYear = years[years.length - 1];
		
		var dataLastYear = [];
		energy.forEach( function(e) {
			if (e.date.slice(0,4) === lastYear) {
				dataLastYear.push(e);
			}
		});
		var lastMonth = 1;
		dataLastYear.slice(0).forEach(function(e) {
			if (e.month > lastMonth) {
				lastMonth = e.month;
			}
		});
		var dataLastMonth = [];
		dataLastYear.slice(0).forEach(function(e) {
			if(e.month === lastMonth) {
				dataLastMonth.push(e);
			}
		});
		var maxCons = 0;
		var maxConsString = "";
		var maxConsDay = "";
		dataLastMonth.slice(0).forEach(function(e) {
			if (e.consumption > maxCons) {
				maxConsString = e.consumption.toString().slice(0,6) + " kW";
				maxCons = e.consumption;
				if (e.day === 1 || e.day === 21 || e.day === 31) {
					maxConsDay = e.day.toString() + "st";
				} else if (e.day === 2 || e.day === 22) {
					maxConsDay = e.day.toString() + "nd";
				} else {
					maxConsDay = e.day.toString() + "th";
				}
			}
		});
		var res = [];
		res.push(maxConsString);
		res.push(maxConsDay)
		return res;
	}
	
	getLastYearData = (_energy) => {
	
		var energy = _energy.slice(0);
		
        energy.forEach(function (d) {
        	var date = new Date(d.date);
        	d.month = date.getMonth() + 1;
        	d.day = date.getDate();
        });
        
		
		var years = this.props.findYears(energy.slice(0));
		var lastYear = years[years.length - 1];
		var dataLastYear = [];
		energy.forEach( function(e) {
			if (e.date.slice(0,4) === lastYear) {
				dataLastYear.push(e);
			}
		});
		
		return dataLastYear;
	}
	
	
	maxConsForMonthsOverLastYear = (dataLastYear) => {
		var maxLastYear = 0;
		var month = "";
		var monthNames = ["January", "February", "March", "April", "May", "June",
		                  "July", "August", "September", "October", "November", "December"
		                ];
		dataLastYear.forEach(function(e) {
			if (e.consumption > maxLastYear) {
				maxLastYear = e.consumption;
				month = monthNames[e.month-1];
			}
		});
		var res = [];
		res.push(maxLastYear.toString().slice(0,6) + " kW");
		res.push(month);
		
		return res;
	}
	
	avgConsPerDayLastMonth = () => {
		var energy = this.getLastYearData(this.state.energyAvgByDay.slice(0));
		
		var lastValue = energy.slice(-1)[0];
		var res = (typeof lastValue !== 'undefined') ? lastValue.consumption.toString().slice(0,6) + " kW"
				: ""; 
		return res;
	}
	
	render () {
		var lastYearData = this.getLastYearData(this.state.energySumByMonth.slice(0));
		var maxConsForDaysOverLastMonth = this.maxConsForDaysOverLastMonth();
		var maxConsForMonthsOverLastYear = this.maxConsForMonthsOverLastYear(lastYearData);
		var avgConsPerDayLastMonth = this.avgConsPerDayLastMonth();
		return(
			<div>
				<ul>
					<p><li>Consumption per day on average for the last month : {avgConsPerDayLastMonth}. </li></p>
					<p><li>Maximum consumption last month : {maxConsForDaysOverLastMonth[0]} the {maxConsForDaysOverLastMonth[1]}.</li></p>
					<p><li>Maximum consumption last year : {maxConsForMonthsOverLastYear[0]} in {maxConsForMonthsOverLastYear[1]}</li></p>
				</ul>
			</div>
		);
	}
}