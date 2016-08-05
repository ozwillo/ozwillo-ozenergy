import React from 'react'
import { render } from 'react-dom'


export class YourConsumption extends React.Component{
	
	constructor(props) {
		super(props);
		this.state = {
				type: "One-Week period",
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
						<td>Individual energy's consumption</td>
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
							    <li><a id="One-week period" href="#" onClick={this.handleChange}>One-Week period</a></li>
							    <li><a id="One-Month period" href="#" onClick={this.handleChange}>One-Month period</a></li>
							    <li><a id="One-Year period" href="#" onClick={this.handleChange}>One-Year period</a></li>
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