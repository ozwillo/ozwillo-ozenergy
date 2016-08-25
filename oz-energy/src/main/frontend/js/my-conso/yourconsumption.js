import React from 'react'
import { render } from 'react-dom'


export class YourConsumption extends React.Component{
	
	handleChange = (e) => {
		this.props.setParentStateType(e.target.id)
	}
	
	handleAggregation = (e) => {
		this.props.setParentStateAgg(e.target.id)
	}

	render() {
		return (
			<div>
				<table className="yourdata">
					<tbody>
					<tr>
						<td>{this.props.title}</td>
						<td>
							<div className="dropdown">
							  <button className="btn btn-success dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
							    {this.props.type} 
							    <span className="caret"></span>
							  </button>
							  <ul className="dropdown-menu" aria-labelledby="dropdownMenu1">
							    <li ><a id="Average" href="#" onClick={this.handleChange}>Average</a></li>
							    <li><a id="Cumulated" href="#" onClick={this.handleChange}>Cumulated</a></li>
							  </ul>
							</div>
						</td>
						<td>
							<div className="dropdown">
							  <button className="btn btn-success dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
							    {this.props.agg} 
							    <span className="caret"></span>
							  </button>
							  <ul className="dropdown-menu" aria-labelledby="dropdownMenu1">
							    <li><a id="By Day" href="#" onClick={this.handleAggregation}>By Day</a></li>
							    <li><a id="By Month" href="#" onClick={this.handleAggregation}>By Month</a></li>
							    <li><a id="By Year" href="#" onClick={this.handleAggregation}>By Year</a></li>
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