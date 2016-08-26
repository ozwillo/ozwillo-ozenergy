import React from 'react'
import { render } from 'react-dom'


export class DataTable extends React.Component{

	render() {
		return (
			<div>
				<EnergyList energies={this.props.energy} unit={this.props.unit} agg={this.props.agg}/>
			</div> 
		);
	}
}


class EnergyList extends React.Component{
	render() {
		var data = this.props.energies;
		var agg = this.props.agg;
        data.forEach(function (d) {
        	var date = new Date(d.date);
        	var year = date.getFullYear();
        	var month = date.getMonth() + 1;
        	var day = date.getDate();
        	if (month < 10) {
        		if(day<10) {
        			if(agg === "By Month") {
        				d.Date = year + "-0" + month;
        			} else if (agg === "By Year") {
        				d.Date = year; 
        			} else {
        				d.Date = year + "-0" + month + "-0" + day;
        			}
        		} else {
        			if(agg === "By Month") {
        				d.Date = year + "-0" + month;
        			}  else if (agg === "By Year") {
        				d.Date = year;
        			} else {
        				d.Date = year + "-0" + month + "-" + day;
        			}
        		}
        	} else {
        		if (day<10) {
        			if(agg === "By Month") {
        				d.Date = year + "-" + month;
        			} else if (agg === "By Year") {
        				d.Date = year;
        			} else {
        				d.Date = year + "-" + month + "-0" + day;
        			}
        			
        		} else {
        			if(agg === "By Month") {
        				d.Date = year + "-" + month;
        			} else if (agg === "By Year") {
        				d.Date = year;
        			} else {
        				d.Date = year + "-" + month + "-" + day;
        			}
        			
        		}
        	}
        });
        
        var energies = data.map((energy,i) =>
            <Energy key={i} energy={energy}/>
        );
        
        return (
        		<div className="table table-condensed table-striped table-responsive">
            <table className="data">
            	<thead>
	                <tr>
	                    <th>Date </th>
	                    <th>Consumption ({this.props.unit})</th>
	                </tr>
                </thead>
                <tbody>
                	{energies}
                </tbody>
            </table>
            </div>
        )
    }
}



class Energy extends React.Component{
	render() {
		return (
			<tr>
				<td>{this.props.energy.Date} </td>
				<td>{this.props.energy.consumption.toString().slice(0,7)}</td>
			</tr>
		);
	}
}