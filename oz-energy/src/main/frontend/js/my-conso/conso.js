/**
 *
 */
import React from 'react'
import { render } from 'react-dom'

import '../../css/specific.css'

import {Today} from './today.js'
import {DataTable} from './datatable.js'
import {LineChart} from './linechart.js'
import {YourConsumption} from './yourconsumption.js'


$(".nav li").removeClass("active");
$('#my').addClass("active");

class App extends React.Component{
	constructor(props) {
		super(props);
		this.state = {
				agg: "By Day",
				type: "Average",
				energy: [],
				mesureUnit: "kWh",
		};
	}

	componentWillMount() {
		this.updateData(this.state.type, this.state.agg);
	}
	
	setParentStateType = (e) => {
		this.setState({type: e});
		this.updateData(e, this.state.agg);
		this.updateMesureUnit(e, this.state.agg);
	}
	
	setParentStateAgg = (e) => {
		this.setState({agg: e});
		this.updateData(this.state.type, e);
		this.updateMesureUnit(this.state.type, e);
	}
	
	endUri = (_type, _agg) => {
		var agg = (_agg === "By Day") ? "day"
				: (_agg === "By Month") ? "month"
				: (_agg === "By Year") ? "year"
				: "day";
		var type = (_type === "Average") ? "avg"
				: (_type === "Cumulated") ? "sum"
				: "avg";
		return "/"+type+"/"+agg;
	}
	
	updateData = (_type, _agg) => {
		var endUri = this.endUri(_type, _agg);
		var reactComponent = this;
		$.ajax({
			url: "/api/my/conso/contract" + endUri,
			type: 'get',
			dataType: 'json',
			success: function (data) {
	            reactComponent.setState({energy: data});
	            reactComponent.setState({energy2: data});
	        },
	        error: function (xhr, status, err) {
	            console.error(status, err.toString());
	        }
		})
	}
	
	updateMesureUnit = (_type, _agg) => {
			var unit = (_type === "Cumulated") ? "kW"
					: (_type === "Average" && _agg === "By Day") ? "kWh"
					: (_type === "Average" && _agg === "By Month") ? "kW/day"
					: (_type === "Average" && _agg === "By Year") ? "kW/day"
					: "kW";
			this.setState({mesureUnit: unit});
	}
	
	render() {
		var energy = this.state.energy.slice(0);
  		return (
  			<div>

				<div className="row">
				
						
					<div className="col-sm-4 col-sm-push-8" >
						<div className="panel panel-success">
							<div className="panel-heading">
								<h3 className="panel-title">Welcome ! </h3>
							</div>
							<div className="panel-body">
								<Today />
							</div>
						</div>
					
						<div className="panel panel-success">
							<div className="panel-heading">
								<h3 className="panel-title"> TODO : Overview </h3>
							</div>
							<div className="panel-body">
								<div className="text">
									Here you will see whether or not your consumption goes up, and in which proportions.
								</div>
							</div>
						</div>
					</div>
						
						
					<div className="col-sm-8 col-sm-pull-4" >
						<div className="panel panel-success">
				            <div className="panel-heading">
				              	<h3 className="panel-title"><YourConsumption title="Individual energy's consumption" 
				              		setParentStateType={this.setParentStateType} setParentStateAgg={this.setParentStateAgg}
				              		type={this.state.type} agg={this.state.agg}/></h3>
				            </div>
				            <div className="panel-body">
								<LineChart energy={energy} type={this.state.type} agg={this.state.agg} updateData ={this.updateData} />
							</div>
			            </div>
  					</div>
  				

				</div>
  				
				
				<div className="row">
				
					<div className="col-sm-6 col-sm-push-6" >
						<div className="panel panel-success">
							<div className="panel-heading">
								<h3 className="panel-title">TODO : Consumption's indicators</h3>
							</div>
							<div className="panel-body">
								<div className="text">
									Here you will see your :
									<ul>
										<li>average consumption of the month</li>
										<li>total consumption of the month</li>
										<li>max total consumption for days/months/year</li>
										<li>...</li>
									</ul>
								</div>
							</div>
						</div>
					</div>
				
					<div className="col-sm-6 col-sm-pull-6" >
						<div className="panel panel-success">
				            <div className="panel-heading">
				              	<h3 className="panel-title"><YourConsumption title="Your Data"
				              		setParentStateType={this.setParentStateType} setParentStateAgg={this.setParentStateAgg}
			              			type={this.state.type} agg={this.state.agg}/></h3>
				            </div>
				            <div className="panel-body">
								<DataTable energy={this.state.energy} unit={this.state.mesureUnit}/>
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
