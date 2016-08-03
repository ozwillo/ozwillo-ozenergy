/**
 *
 */
import React from 'react'
import { render } from 'react-dom'
import d3 from 'd3'

import '../css/specific.css'


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

var DataTable = React.createClass({
	getInitialState: function() {
		return {energy: []};
	},
	componentWillMount: function() {
		$.ajax({
			url: "/api/my/conso/" + ck_tmp + "/day",
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
			<div>
				<EnergyList energies={this.state.energy} />
			</div> 
		);
	}
})


{/* test */}
var EnergyList = React.createClass({
	render: function () {
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
        )
    }
})



var Energy = React.createClass({
	render: function () {
		return (
			<tr>
				<td>{this.props.energy.Date} </td>
				<td>{this.props.energy.consumption.toString().slice(0,7)}</td>
			</tr>
		);
	}
})

var Axis=React.createClass({
    propTypes: {
        h:React.PropTypes.number,
        axis:React.PropTypes.func,
        axisType:React.PropTypes.oneOf(['x','y'])

    },

    componentDidUpdate: function () { this.renderAxis(); },
    componentDidMount: function () { this.renderAxis(); },
    renderAxis: function () {
        var node = ReactDOM.findDOMNode(this);
        if (this.props.axisType=='x') {
        	d3.select(node).call(this.props.axis)
        		.selectAll("text")
        			.style("text-anchor", "end")
        			.attr("dx", "-.8em")
        			.attr("dy", ".15em")
        			.attr("transform", function(d) {
        				return "rotate(-65)" 
                });	
        } else {
            d3.select(node).call(this.props.axis);     	
        }

    },
    render: function () {

        var translate = "translate(0,"+(this.props.h)+")";

        return (
            <g className="axis" transform={this.props.axisType=='x'?translate:""} >
            </g>
        );
    }

});

var Grid=React.createClass({
    propTypes: {
        h:React.PropTypes.number,
        grid:React.PropTypes.func,
        gridType:React.PropTypes.oneOf(['x','y'])
    },

    componentDidUpdate: function () { this.renderGrid(); },
    componentDidMount: function () { this.renderGrid(); },
    renderGrid: function () {
        var node = ReactDOM.findDOMNode(this);
        d3.select(node).call(this.props.grid);

    },
    render: function () {
        var translate = "translate(0,"+(this.props.h)+")";
        return (
            <g className="y-grid" transform={this.props.gridType=='x'?translate:""}>
            </g>
        );
    }

});


var Dots=React.createClass({
    propTypes: {
        data:React.PropTypes.array,
        x:React.PropTypes.func,
        y:React.PropTypes.func

    },
    render:function(){

        var _self=this;

        //remove last & first point
        var data=this.props.data.splice(1);
        data.pop();

        var circles=data.map(function(d,i){

            return (<circle className="dot" r="7" cx={_self.props.x(d.Date)} 
					cy= {_self.props.y(d.consumption)} fill="#000000"
					stroke="#ffffff" strokeWidth="5px" key={i} 
		            data-key={d3.time.format("%Y-%m-%d")(d.Date)} data-value={d.consumption}/>);
        });

        return(
            <g>
                {circles}
            </g>
        );
    }
});

var LineChart=React.createClass({

    propTypes: {
        width:React.PropTypes.number,
        height:React.PropTypes.number,
        chartId:React.PropTypes.string,
    },
    
    getDefaultProps: function() {
        return {
            width: 600,
            height: 300,
            chartId: 'v1_chart'
        };
    },
    getInitialState:function(){
        return {
        	energy: [],
        	width:this.props.width
        };
    },
    
    componentWillMount:function(){
        var _self=this;
        $(window).on('resize', function(e) {
            _self.updateSize();
        });
        this.setState({width:this.props.width});

    },
	componentDidMount: function() {
		this.updateSize();
		this.updateData();
	},
	
    componentWillUnmount:function(){
    	$(window).off('resize');
    	this.updateData();
    },

	updateSize:function(){
	    var node = ReactDOM.findDOMNode(this);
	    var parentWidth=$(node).width();
	
	    if(parentWidth<this.props.width){
	        this.setState({width:parentWidth-20});
	    }else{
	        this.setState({width:this.props.width});
	    }
	    this.updateData();
	},
	
	updateData: function() {
		$.ajax({
			url: "/api/my/conso/" + ck_tmp + "/day",
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
    
    render:function(){
		
		var data = this.state.energy;
        var margin = {top: 5, right: 50, bottom: 90, left: 50},
            w = this.state.width - (margin.left + margin.right),
            h = this.props.height - (margin.top + margin.bottom);

        var i = 0;
        data.forEach(function (d) {
        	i++;
        	var date = new Date(d.date);
            d.Date = new Date(date.getFullYear(), date.getMonth(), date.getDate());
            console.log(i+":"+d.Date);
        });

        
        var x = d3.time.scale()
            .domain(d3.extent(data, function (d) {
                return d.Date;
            }))
            .rangeRound([0, w]);

        var y = d3.scale.linear()
            .domain([0,d3.max(data,function(d){
                return d.consumption+0.2;
            })])
            .range([h, 0]);
        
       var yAxis = d3.svg.axis()
	        .scale(y)
	        .orient('left')
	        .ticks(5);

	   var xAxis = d3.svg.axis()
	        .scale(x)
	        .orient('bottom')
	        .tickValues(data.map(function(d,i){
	            if(i>0)
	                return d.Date;
	        }).splice(1))
	        .ticks(4)
	        .tickFormat(d3.time.format("%Y-%m-%d")); 
	   
	    var yGrid = d3.svg.axis()
	        .scale(y)
	        .orient('left')
	        .ticks(5)
	        .tickSize(-w, 0, 0)
	        .tickFormat("");
        
        var line = d3.svg.line()
            .x(function (d) {
                return x(d.Date);
            })
            .y(function (d) {
                return y(d.consumption);
            }).interpolate('cardinal');


        var transform='translate(' + margin.left + ',' + margin.top + ')';

        return (
            <div>
                <svg id={this.props.chartId} width={this.state.width} height={this.props.height}>

                    <g transform={transform}>
	                    <Grid h={h} grid={yGrid} gridType="y"/>
	                    <Axis h={h} axis={yAxis} axisType="y" />
	                    <Axis h={h} axis={xAxis} axisType="x"/>
                        <path className="line shadow" d={line(data)} strokeLinecap="round"/>
	                    <Dots data={data} x={x} y={y}/>
                    </g>
                </svg>
            </div>
        );
    }
});

var Today = React.createClass({
	render: function(){
		var d = new Date();
		var date = d.toDateString();
		var time = d.toTimeString();
		return(
			<div>
				<p>We are {date}.</p>
				<p>It is {time.slice(0,8)}.</p>
			</div>
		);
	}

})


var App = React.createClass({
  	render: function(){
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
				              	<h3 className="panel-title">Individual energy's consumption </h3>
				            </div>
				            <div className="panel-body">
								<LineChart />
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
				              	<h3 className="panel-title">Your data</h3>
				            </div>
				            <div className="panel-body">
								<DataTable />
				            </div>
				          </div>
					</div>
				
					
				</div>
			
  			</div>
  		);
  	}
  });

ReactDOM.render(
		<App/>,
		document.getElementById('container')
);
