import React from 'react'
import { render } from 'react-dom'


export class ConsumptionChoice extends React.Component{
	
	handleChange = (e) => {
		this.props.setParentStateCity(e.target.id)
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
							    {this.props.city} 
							    <span className="caret"></span>
							  </button>
							  <ul className="dropdown-menu" aria-labelledby="dropdownMenu1">
							    <li><a id="Clermont-Ferrand" href="#" onClick={this.handleChange}>Clermont-Ferrand</a></li>
							    <li ><a id="Colmar" href="#" onClick={this.handleChange}>Colmar</a></li>
							    <li ><a id="Lille" href="#" onClick={this.handleChange}>Lille</a></li>
							    <li><a id="Lyon" href="#" onClick={this.handleChange}>Lyon</a></li>
							    <li ><a id="Massy" href="#" onClick={this.handleChange}>Massy</a></li>
							    <li ><a id="Montpellier" href="#" onClick={this.handleChange}>Montpellier</a></li>
							    <li ><a id="Nantes" href="#" onClick={this.handleChange}>Nantes</a></li>
							    <li><a id="Paris" href="#" onClick={this.handleChange}>Paris</a></li>
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