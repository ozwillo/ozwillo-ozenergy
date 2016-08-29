/**
 *
 */
import React from 'react'
import { render } from 'react-dom'

import '../../css/specific.css'

import {Today} from './today.js'
import {DataTable} from './datatable.js'
import {LineChart} from './linechart.js'
import {ConsumptionChoice} from './consumptionchoice.js'


$(".nav li").removeClass("active");
$('#global').addClass("active");

class App extends React.Component{
	constructor(props) {
		super(props);
		this.state = {
				agg: "By Day",
				city: "Paris", // TODO : remplace type par city et changer les fonctions (ex: unité : se référer aux notes, plus de type, ...)
				energy: [],
				measureUnit: "kW",
				year: "Year",
				start: 0,
		};
	}

	componentWillMount() {
		this.updateData(this.state.city, this.state.agg, "Year");
		this.setParentStateStart(0);
	}
	
	setParentStateYear = (e) => {
		this.setState({year: e});
		this.setParentStateStart(0);
	}
	
	setParentStateStart = (e) => {
		this.setState({start: e});
	}
	
	setParentStateCity = (e) => {
		this.setState({city: e});
		this.updateData(e, this.state.agg, "Year");
		this.setParentStateStart(0);
	}
	
	setParentStateAgg = (e) => {
		this.setState({agg: e});
		this.updateData(this.state.city, e, "Year");
		this.setParentStateStart(0);
	}
	
	endUri = (_city, _agg) => {
		var agg = (_agg === "By Day") ? "day"
				: (_agg === "By Month") ? "month"
				: (_agg === "By Year") ? "year"
				: "day";
		return "/"+_city+"/"+agg;
	}
	
	updateData = (_city, _agg, _year) => {
		var endUri = this.endUri(_city, _agg);
		var reactComponent = this;
		var firstYear = "2011";
		$.ajax({
			url: "/api/my/conso/city" + endUri,
			type: 'get',
			dataType: 'json',
			success: function (data) {
	            reactComponent.setState({energy: data});
	            firstYear = data[0].date.slice(0,4);
	            if(_year === "Year") {
	            	reactComponent.setState({year: firstYear});
	            }
	        },
	        error: function (xhr, status, err) {
	            console.error(status, err.toString());
	        }
		});
	}
	
	findYears = (energy) => {
		var years = [];
		energy.forEach(function(e) {
			if(!years.includes(e.date.slice(0,4))){
				years.push(e.date.slice(0,4));
			}
		});
		return years;
	}
	
	render() {
		var energy = this.state.energy.slice(0);
		var years = this.findYears(energy);
		var consumptionTitle = "Average of energy's consumption (in " + this.state.measureUnit + ")";
		var agg = this.state.agg.slice(0);
  		return (
  			<div>

				<div className="row">
				
						
					<div className="col-sm-4" >
						<div className="panel panel-success">
							<div className="panel-heading">
								<h3 className="panel-title"><ConsumptionChoice title="Data"
								setParentStateCity={this.setParentStateCity} setParentStateAgg={this.setParentStateAgg}
								city={this.state.city} agg={this.state.agg} /></h3>
							</div>
							<div className="panel-body">
								<DataTable energy={this.state.energy} unit={this.state.measureUnit} agg={agg}/>
							</div>
						</div>
					</div>
						
						
					<div className="col-sm-8" >
						<div className="panel panel-success">
				            <div className="panel-heading">
				              	<h3 className="panel-title"><ConsumptionChoice title={consumptionTitle}
				              		setParentStateCity={this.setParentStateCity} setParentStateAgg={this.setParentStateAgg}
				              		city={this.state.city} agg={this.state.agg} /></h3>
				            </div>
				            <div className="panel-body">
								<LineChart energy={energy} city={this.state.city} agg={this.state.agg} 
								updateData ={this.updateData} setParentStateYear={this.setParentStateYear}
								setParentStateStart={this.setParentStateStart} start={this.state.start}
								years={years} year={this.state.year} />
							</div>
			            </div>
  					</div>
  				

				</div>
  				
			
  			</div>
  		);
  	}
  }

ReactDOM.render(
		<App/>,
		document.getElementById('container')
);
