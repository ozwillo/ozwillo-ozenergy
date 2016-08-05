import React from 'react'
import { render } from 'react-dom'

export class YourData extends React.Component{
	
	constructor() {
		super();
		this.state = {
				type: "Day",
				agg: "Average"
		};
	}

	handleChange = (e) => {
		this.setState({type: e.target.id});
	}
	handleAggregation = (e) => {
		this.setState({agg: e.target.id});
	}

	render() {
		return (
			<div>
				<table className="yourdata">
					<tbody>
					<tr>
						<td>Your Data</td>
						<td>
							<div className="dropdown">
							  <button className="btn btn-success dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
							    {this.state.agg} 
							    <span className="caret"></span>
							  </button>
							  <ul className="dropdown-menu" aria-labelledby="dropdownMenu1">
							    <li ><a id="Average" href="#" onClick={this.handleAggregation}>Average</a></li>
							    <li><a id="Cumulated" href="#" onClick={this.handleAggregation}>Cumulated</a></li>
							  </ul>
							</div>
						</td>
						<td>
							<div className="dropdown">
							  <button className="btn btn-success dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
							    {this.state.type} 
							    <span className="caret"></span>
							  </button>
							  <ul className="dropdown-menu" aria-labelledby="dropdownMenu1">
							    <li ><a id="Day" href="#" onClick={this.handleChange}>Day</a></li>
							    <li><a id="Week" href="#" onClick={this.handleChange}>Week</a></li>
							    <li><a id="Month" href="#" onClick={this.handleChange}>Month</a></li>
							    <li><a id="Year" href="#" onClick={this.handleChange}>Year</a></li>
							  </ul>
							</div>
						</td>
					</tr>
					</tbody>
				</table>
			</div>
		);
	}
}