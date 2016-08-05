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
import {YourData} from './yourdata.js'


$(".nav li").removeClass("active");
$('#my').addClass("active");

var ck_tmp = 8170837;

{/*
var DataAll = React.createClass({
	getInitialState: function() {
		return {energy: []};
	},
	componentDidMount: function() {
		$.ajax({
			//url: "/api/my/conso/" + default_app.id,
			url: "/api/my/conso/" + ck_tmp,
			type: 'get',
			dataType: 'json',
			success: function (data) {
                var state = this.state;
                state.energy = data;
                this.setState(state);
            }.bind(this),
            error: function (xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
		})
	},
	render: function () {
		return (
				<EnergyList energies={this.state.energy} />
		);
	}
})
*/}

class App extends React.Component{
  	render() {
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
				              	<h3 className="panel-title"><YourConsumption/></h3>
				            </div>
				            <div className="panel-body">
								<LineChart ck_tmp={ck_tmp}/>
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
				              	<h3 className="panel-title"><YourData /></h3>
				            </div>
				            <div className="panel-body">
								<DataTable ck_tmp={ck_tmp} />
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
