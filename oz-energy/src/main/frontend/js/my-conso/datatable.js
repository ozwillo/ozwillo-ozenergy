import React from 'react'
import { render } from 'react-dom'


export class DataTable extends React.Component{

	constructor() {
		super();
		this.state = {energy: []};
	}

	componentWillMount() {
		$.ajax({
			url: "/api/my/conso/" + this.props.ck_tmp + "/day",
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
	}
	render() {
		return (
			<div>
				<EnergyList energies={this.state.energy} />
			</div> 
		);
	}
}


class EnergyList extends React.Component{
	render() {
		var data = this.props.energies;
		
        data.forEach(function (d) {
        	var date = new Date(d.date);
        	var year = date.getFullYear();
        	var month = date.getMonth() + 1;
        	var day = date.getDate();
        	if (month < 10) {
        		if(day<10) {
        			d.Date = year + "-0" + month + "-0" + day;
        		} else {
        			d.Date = year + "-0" + month + "-" + day;
        		}
        	} else {
        		if (day<10) {
        			d.Date = year + "-" + month + "-0" + day;
        		} else {
        			d.Date = year + "-" + month + "-" + day;
        		}
        	}
        });
        
        var energies = data.map((energy,i) =>
            <Energy key={i} energy={energy}/>
        );
        
        return (
        		<div className="table-responsive">
            <table className="data">
            	<thead>
	                <tr>
	                    <th>Date </th>
	                    <th>Consumption (kWh)</th>
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