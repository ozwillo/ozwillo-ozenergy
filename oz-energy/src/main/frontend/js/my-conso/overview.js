import React from 'react'
import { render } from 'react-dom'

export class Overview extends React.Component {
	
	constructor(props) {
		super(props);
		this.state = {
				energy: [],
				measureUnit: "kW",
		};
	}
	
	componentDidMount () {
		this.updateData()
	}
	
	updateData = () => {
		var reactComponent = this;
		$.ajax({
			url: "/api/my/conso/contract/sum/month",
			type: 'get',
			dataType: 'json',
			success: function (data) {
	            reactComponent.setState({energy: data});
	        },
	        error: function (xhr, status, err) {
	            console.error(status, err.toString());
	        }
		});
	}
	
	render() {
		var energy = this.state.energy.slice(0);
        energy.forEach(function (d) {
        	var date = new Date(d.date);
        	d.year = date.getFullYear();
        	d.month = date.getMonth() + 1;
        	d.day = date.getDate();
        });
		var years = this.props.findYears(energy);
		var lastYear = years[years.length - 1];
		var dataLastYear = [];
		energy.forEach( function(e) {
			if (e.date.slice(0,4) === lastYear) {
				dataLastYear.push(e);
			}
		});
		
		var lastMonth = 1;
		dataLastYear.forEach( function(e) {
			if (e.month > lastMonth) {
				lastMonth = e.month;	
			}
		});
		var consLastMonth = 0;
		var consBeforeLastMonth = 0;
		dataLastYear.forEach( function(e) {
			if (e.month === lastMonth) {
				consLastMonth = e.consumption;
			} else if (e.month === lastMonth - 1) {
				consBeforeLastMonth = e.consumption;
			}
		});
		
		var lastMonth = consLastMonth.toString().slice(0,6) + " kW"
		var percent = (consLastMonth - consBeforeLastMonth) / consBeforeLastMonth;
		var msg = "";
		var msg2 = "It reached " + consLastMonth + " kW last month.";
		var percentMsg = "";
		var img = "glyphicon glyphicon-triangle-top";
		var img2 = "";
		if (consLastMonth > consBeforeLastMonth) {
			msg = "Your consumption raised of " + percent.toString().slice(0,4) + "% the last month.";
			img = "glyphicon glyphicon-triangle-top";
			percentMsg = percent.toString().slice(0,4) + "%"
		} else if (consLastMonth < consBeforeLastMonth) {
			msg =  "Your consumption decreased of " + (-1*percent).toString().slice(0,4) + "% the last month.";
			percent = -1 * percent
			img = "glyphicon glyphicon-triangle-bottom";
			percentMsg = percent.toString().slice(0,4) + "%"
		} else if (consLastMonth === consBeforeLastMonth){
			msg = "Your consumption remains at " + lastMonth + ".";
			img = "glyphicon glyphicon-triangle-left";
			img2 = "glyphicon glyphicon-triangle-right";
			percentMsg = lastMonth;
			msg2 = "";
			lastMonth = "";
		}
		
		return (
			<div>
				<p> <span className="indicator"><span className={img}></span><span className={img2}></span>  {percentMsg} </span></p>
				<p>{msg}</p>
				<p><span className="indicator">{lastMonth}</span></p>
				<p>{msg2}</p>
			</div>
		);
	}
}