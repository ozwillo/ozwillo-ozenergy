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



var DataByDay = React.createClass({
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
//			success: function(data) {
//				this.setState({energy: data})
//			}.bind(this),
		})
	},
	render: function () {
		return (
				<div>
					
					<p> Extract from your data :</p>
					<EnergyList energies={this.state.energy} />
					
					<div><LineChart energies={this.state.energy} /> </div>

				</div>
		);
	}
})

{/* test */}
var EnergyList = React.createClass({
	render: function () {
        var energies = this.props.energies.map((energy,i) =>
            <Energy key={i} energy={energy}/>
        );
        
        return (
            <table>
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
				<td>{this.props.energy.date} </td>
				<td>{this.props.energy.consumption}</td>
			</tr>
		);
	}
})

//var resizeMixin={
//	    componentWillMount:function(){
//
//	        var _self=this;
//
//	        $(window).on('resize', function(e) {
//	            _self.updateSize();
//	        });
//
//	        this.setState({width:this.props.width});
//
//	    },
//	    componentDidMount: function() {
//	        this.updateSize();
//	    },
//	    componentWillUnmount:function(){
//	        $(window).off('resize');
//	    },
//
//	    updateSize:function(){
//	        var node = ReactDOM.findDOMNode(this);
//	        var parentWidth=$(node).width();
//
//	        if(parentWidth<this.props.width){
//	            this.setState({width:parentWidth-20});
//	        }else{
//	            this.setState({width:this.props.width});
//	        }
//	    }
//	};


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
        d3.select(node).call(this.props.axis);

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


//var ToolTip=React.createClass({
//    propTypes: {
//        tooltip:React.PropTypes.object
//    },
//    render:function(){
//
//        var visibility="hidden";
//        var transform="";
//        var x=0;
//        var y=0;
//        var width=150,height=70;
//        var transformText='translate('+width/2+','+(height/2-5)+')';
//        var transformArrow="";
//
//        if(this.props.tooltip.display==true){
//            var position = this.props.tooltip.pos;
//
//            x= position.x;
//            y= position.y;
//            visibility="visible";
//
//            //console.log(x,y);
//
//            if(y>height){
//                transform='translate(' + (x-width/2) + ',' + (y-height-20) + ')';
//                transformArrow='translate('+(width/2-20)+','+(height-2)+')';
//            }else if(y<height){
//
//                transform='translate(' + (x-width/2) + ',' + (Math.round(y)+20) + ')';
//                transformArrow='translate('+(width/2-20)+','+0+') rotate(180,20,0)';
//            }
//
//
//
//        }else{
//            visibility="hidden"
//        }
//
//        return (
//            <g transform={transform}>
//                <rect class="shadow" is width={width} height={height} rx="5" ry="5" visibility={visibility} fill="#6391da" opacity=".9"/>
//                <polygon class="shadow" is points="10,0  30,0  20,10" transform={transformArrow}
//                         fill="#6391da" opacity=".9" visibility={visibility}/>
//                <text is visibility={visibility} transform={transformText}>
//                    <tspan is x="0" text-anchor="middle" font-size="15px" fill="#ffffff">{this.props.tooltip.data.key}</tspan>
//                    <tspan is x="0" text-anchor="middle" dy="25" font-size="20px" fill="#a9f3ff">{this.props.tooltip.data.value+" kWh"}</tspan>
//                </text>
//            </g>
//        );
//    }
//});

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
					cy= {_self.props.y(d.consumption)} fill="#000080"
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

    //mixins:[resizeMixin],
    
    getDefaultProps: function() {
        return {
            width: 600,
            height: 300,
            chartId: 'v1_chart'
        };
    },
    getInitialState:function(){
        return {
        	width:this.props.width
        };
    },
    
    render:function(){
		
		var data = this.props.energies;
		
        var margin = {top: 5, right: 50, bottom: 20, left: 50},
            w = this.state.width - (margin.left + margin.right),
            h = this.props.height - (margin.top + margin.bottom);

//        var parseDate = d3.time.format("%Y-%m-%d").parse;
//        var strictIsoParse = d3.time.format("%Y-%m-%dT%H:%M:%S.%LZ").parse;
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
	        .ticks(4);
	
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
	                    <Dots data={data} x={x} y={y} />
                    </g>
                </svg>
            </div>
        );
    },
//    showToolTip:function(e){
//        e.target.setAttribute('fill', '#A9A9A9');
//
//        this.setState({tooltip:{
//            display:true,
//            data: {
//                key:e.target.getAttribute('data-key'),
//                value:e.target.getAttribute('data-value')
//                },
//            pos:{
//                x:e.target.getAttribute('cx'),
//                y:e.target.getAttribute('cy')
//            }
//
//            }
//        });
//    },
//    hideToolTip:function(e){
//        e.target.setAttribute('fill', '#000080');
//        this.setState({tooltip:{ display:false,data:{key:'',value:''}}});
//    }
});


var App = React.createClass({
  	render: function(){
  		return (
  			<div>
  				<p> individual energy's consumption </p>
  			
  				<DataByDay />
  			</div>
  		);
  	}
  });

ReactDOM.render(
		<App/>,
		document.getElementById('container')
);
