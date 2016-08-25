import React from 'react'
import { render } from 'react-dom'
import d3 from 'd3'


class Axis extends React.Component{

    componentDidUpdate() { this.renderAxis(); }
    
    componentDidMount() { this.renderAxis(); }
    
    renderAxis() {
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
    }
    
    render() {
        var translate = "translate(0,"+(this.props.h)+")";
        return (
            <g className="axis" transform={this.props.axisType=='x'?translate:""} >
            </g>
        );
    }

}

Axis.propTypes = {
    h:React.PropTypes.number,
    axis:React.PropTypes.func,
    axisType:React.PropTypes.oneOf(['x','y'])
};

class Grid extends React.Component{

    componentDidUpdate() { this.renderGrid(); }
    componentDidMount() { this.renderGrid(); }
    renderGrid() {
        var node = ReactDOM.findDOMNode(this);
        d3.select(node).call(this.props.grid);

    }
    render() {
        var translate = "translate(0,"+(this.props.h)+")";
        return (
            <g className="y-grid" transform={this.props.gridType=='x'?translate:""}>
            </g>
        );
    }

}

Grid.propTypes = {
    h:React.PropTypes.number,
    grid:React.PropTypes.func,
    gridType:React.PropTypes.oneOf(['x','y'])
};


class Dots extends React.Component{

    render(){

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
}
        
Dots.propTypes = {
    data:React.PropTypes.array,
    x:React.PropTypes.func,
    y:React.PropTypes.func

};

export class LineChart extends React.Component{
	constructor(props) {
		super(props);
		this.state = {
				energy: [],
	        	width: this.props.width,
	        	start: 0,
	        };
	}

    componentWillMount(){
        var _self=this;
        $(window).on('resize', function(e) {
            _self.updateSize();
        });
        this.setState({width:this.props.width});
    }
    
	componentDidMount() {
		this.props.updateData(this.props.type, this.props.agg);
	}
	
    componentWillUnmount() {
    	$(window).off('resize');
    	this.props.updateData(this.props.type, this.props.agg);
    }
    
	updateSize = () => {
	    var node = ReactDOM.findDOMNode(this);
	    var parentWidth=$(node).width();
	
	    if(parentWidth<this.props.width){
	        this.setState({width:parentWidth-20});
	    }else{
	        this.setState({width:this.props.width});
	    }
	    this.props.updateData(this.props.type, this.props.agg);
	}
    
	lengthDisplay = () => {
		return (this.props.agg === "By Day") ? 7
				: (this.props.agg === "By Month") ? 6
				: (this.props.agg === "By Year") ? this.props.energy.slice(0).length
				: 7;
	}
	
	dataForChart = () => {
		var start = this.state.start;
		return data = this.props.energy.slice(start, start + this.lengthDisplay());
	}
	
	previous = () => {
		var actual = this.state.start;
		var length = this.props.energy.slice(0).length;
		var lengthDisplay = this.lengthDisplay();
		var previous = actual - lengthDisplay;
		if (lengthDisplay > 1 && actual > lengthDisplay - 1) {
			this.setState({start: previous});
		}
		this.props.updateData(this.props.type, this.props.agg);
	}
	
	next = () => {
		var actual = this.state.start;
		var length = this.props.energy.slice(0).length;
		var lengthDisplay = this.lengthDisplay();
		var next = actual + lengthDisplay;
		if (actual < length - lengthDisplay) {
			this.setState({start: next});
		}
		this.props.updateData(this.props.type, this.props.agg);
	}
	
	
    render() {
    	//var data = this.props.energy.slice(0);
    	var data = this.props.energy.slice(this.state.start, this.state.start + this.lengthDisplay());
    	var type = this.props.type.slice(0);
        var margin = {top: 5, right: 50, bottom: 90, left: 50},
            w = this.state.width - (margin.left + margin.right),
            h = this.props.height - (margin.top + margin.bottom);

        data.forEach(function (d) {
        	var date = new Date(d.date);
            d.Date = new Date(date.getFullYear(), date.getMonth(), date.getDate());
        });

        var x = d3.time.scale()
            .domain(d3.extent(data, function (d) {
                return d.Date;
            }))
            .rangeRound([0, w]);
        
        var add = (type === "Average") ? 0.2
        		: (type === "Cumulated") ? 10
        		: 100;
        
        var y = d3.scale.linear()
            .domain([0,d3.max(data,function(d){
                return d.consumption+add;
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
	            <div className="right">
	            	<button type="button" className="btn btn-default btn-sm" onClick={this.previous}><span className="glyphicon glyphicon-arrow-left"> </span></button>
	            	<button type="button" className="btn btn-default btn-sm" onClick={this.next}> <span className="glyphicon glyphicon-arrow-right"></span></button>
	            </div>
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
            </div>
        );
    }
}

LineChart.propTypes = {
    width:React.PropTypes.number,
    height:React.PropTypes.number,
    chartId:React.PropTypes.string,
};

LineChart.defaultProps = {
    width: window.innerWidth*0.40,
    height: 300,
    chartId: 'v1_chart'
};